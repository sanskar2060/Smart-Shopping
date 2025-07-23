import React from 'react';
import Header from '../../components/Header/Header';
import Footer from '../../components/Footer/Footer';

const Home = () => {
  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <Header />
      
      <main className="flex-grow container mx-auto px-4 py-8">
        {/* About Section */}
        <section id="about" className="mb-12">
          <h2 className="text-3xl font-bold text-gray-800 mb-6">About PriceCompare</h2>
          <div className="bg-white p-6 rounded-lg shadow-md">
            <p className="text-gray-700 mb-4">
              PriceCompare is your ultimate destination for comparing prices across multiple online retailers. 
              Our mission is to help you find the best deals and save money on your purchases.
            </p>
            <p className="text-gray-700">
              We aggregate prices from various e-commerce platforms to provide you with a comprehensive 
              view of the market, ensuring you always get the best value for your money.
            </p>
          </div>
        </section>

        {/* Contact Section */}
        <section id="contact" className="mb-12">
          <h2 className="text-3xl font-bold text-gray-800 mb-6">Contact Us</h2>
          <div className="bg-white p-6 rounded-lg shadow-md">
            <div className="mb-4">
              <h3 className="text-xl font-semibold text-gray-800 mb-2">Email</h3>
              <p className="text-gray-700">support@pricecompare.com</p>
            </div>
            <div className="mb-4">
              <h3 className="text-xl font-semibold text-gray-800 mb-2">Phone</h3>
              <p className="text-gray-700">+1 (555) 123-4567</p>
            </div>
            <div>
              <h3 className="text-xl font-semibold text-gray-800 mb-2">Address</h3>
              <p className="text-gray-700">123 PriceCompare Street, Suite 100</p>
              <p className="text-gray-700">San Francisco, CA 94107</p>
            </div>
          </div>
        </section>
      </main>

      <Footer />
    </div>
  );
};

export default Home;