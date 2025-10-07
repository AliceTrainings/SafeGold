const API_KEY = "goldapi-5z18ld4gkwkcye86-io"; // Replace with your real API key
const API_URL = `https://metals-api.com/api/latest?access_key=${API_KEY}&base=INR&symbols=XAU,XAG,XPT`;

async function fetchMetalPrices() {
    try {
        const response = await fetch(API_URL);
        const data = await response.json();

        document.getElementById("gold-price").textContent = `₹${data.rates.XAU.toFixed(2)}`;
        document.getElementById("silver-price").textContent = `₹${data.rates.XAG.toFixed(2)}`;
        document.getElementById("platinum-price").textContent = `₹${data.rates.XPT.toFixed(2)}`;
    } catch (err) {
        console.error("Error fetching metal prices:", err);
    }
}

// Initial fetch
fetchMetalPrices();
// Update every 5 minutes
setInterval(fetchMetalPrices, 300000);
