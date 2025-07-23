# ShopSmartSaveMore

# for flipcart-api-config-docker

# Build the image
docker build -t flipkart-scraper-api .

# Run the container (background)
docker run -d -p 3000:3000 --name scraper flipkart-scraper-api
