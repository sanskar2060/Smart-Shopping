from flask import Flask, request, jsonify
import requests
from bs4 import BeautifulSoup
from urllib.parse import quote
import re

app = Flask(__name__)

# Set headers to mimic a browser visit
HEADERS = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36',
    'Accept-Language': 'en-US, en;q=0.5'
}

def scrape_product_page(asin):
    """Scrape product details from Amazon product page"""
    url = f"https://www.amazon.com/dp/{asin}"
    
    try:
        response = requests.get(url, headers=HEADERS)
        response.raise_for_status()
        
        soup = BeautifulSoup(response.text, 'html.parser')
        
        # Extract product details
        product = {
            'asin': asin,
            'url': url,
            'title': get_text(soup, 'span', {'id': 'productTitle'}),
            'price': get_text(soup, 'span', {'class': 'a-price-whole'}),
            'description': get_description(soup),
            'rating': get_text(soup, 'span', {'class': 'a-icon-alt'}),
            'image': get_image(soup)
        }
        
        return product
    
    except requests.exceptions.RequestException as e:
        return {'error': str(e)}

def get_text(soup, tag, attrs):
    """Helper function to safely extract text"""
    element = soup.find(tag, attrs)
    return element.get_text(strip=True) if element else None

def get_description(soup):
    """Extract product description from different possible sections"""
    # Try feature bullets
    feature_bullets = soup.find('div', {'id': 'feature-bullets'})
    if feature_bullets:
        bullets = feature_bullets.find_all('span', {'class': 'a-list-item'})
        return "\n".join([bullet.get_text(strip=True) for bullet in bullets])
    
    # Try product description
    desc_section = soup.find('div', {'id': 'productDescription'})
    if desc_section:
        return desc_section.get_text(strip=True)
    
    # Try A+ content
    aplus = soup.find('div', {'id': 'aplus'})
    if aplus:
        return aplus.get_text(strip=True)
    
    return None

def get_image(soup):
    """Extract product image URL"""
    img = soup.find('img', {'id': 'landingImage'})
    return img.get('src') if img else None

def search_products_by_description(description):
    """Search Amazon products by description and return first few results"""
    search_url = f"https://www.amazon.com/s?k={quote(description)}"
    
    try:
        response = requests.get(search_url, headers=HEADERS)
        response.raise_for_status()
        
        soup = BeautifulSoup(response.text, 'html.parser')
        products = []
        
        # Extract search results
        results = soup.find_all('div', {'data-component-type': 's-search-result'})
        
        for result in results[:100]:  # Limit to first 5 results
            asin = result.get('data-asin')
            if not asin:
                continue
                
            title = get_text(result, 'span', {'class': 'a-text-normal'})
            price = get_text(result, 'span', {'class': 'a-price-whole'})
            rating = get_text(result, 'span', {'class': 'a-icon-alt'})
            image = result.find('img', {'class': 's-image'})
            image_url = image.get('src') if image else None
            
            products.append({
                'asin': asin,
                'title': title,
                'price': price,
                'rating': rating,
                'image': image_url,
                'url': f"https://www.amazon.com/dp/{asin}"
            })
        
        return products
    
    except requests.exceptions.RequestException as e:
        return {'error': str(e)}

@app.route('/product/<asin>', methods=['GET'])
def get_product(asin):
    """Endpoint to get product by ASIN"""
    # Validate ASIN format (Amazon Standard Identification Number)
    if not re.match(r'^[A-Z0-9]{10}$', asin):
        return jsonify({'error': 'Invalid ASIN format'}), 400
    
    product = scrape_product_page(asin)
    return jsonify(product)

@app.route('/', methods=['GET'])
def home():
    return "amazon scraper"


@app.route('/search', methods=['GET'])
def search_products():
    """Endpoint to search products by description"""
    query = request.args.get('q')
    if not query:
        return jsonify({'error': 'Missing search query parameter "q"'}), 400
    
    results = search_products_by_description(query)
    return jsonify({'query': query, 'results': results})

if __name__ == '__main__':
    import argparse
    parser = argparse.ArgumentParser()
    parser.add_argument('--port', type=int, default=5000, help='Port to run the Flask app on')
    args = parser.parse_args()
    
    app.run(host='0.0.0.0', port=args.port, debug=False)  # debug=False for production