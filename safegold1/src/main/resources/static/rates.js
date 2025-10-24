const API_KEY = "c45bbc35b2742eacfc58e298bc1fd3b6"; // Your API key
const API_URL = "https://api.metalpriceapi.com/v1/latest";
const GRAM_CONVERSION = 31.1035; // 1 troy ounce = 31.1035 grams

document.addEventListener("DOMContentLoaded", () => {
    // DOM Elements
    const goldEl = document.getElementById("gold-price");
    const silverEl = document.getElementById("silver-price");
    const platinumEl = document.getElementById("platinum-price");

    // Fetch metal prices per gram
    async function fetchMetalPricesPerGram() {
        try {
            const response = await fetch(`${API_URL}?api_key=${API_KEY}&base=USD&currencies=XAU,XAG,XPT,INR`);
            if (!response.ok) throw new Error("API fetch failed");

            const data = await response.json();

            // Calculate per gram in INR
            const usdToInr = data.rates.INR || 1;
            const goldPerGram = ((data.rates.XAU || 0) * usdToInr / GRAM_CONVERSION).toFixed(2);
            const silverPerGram = ((data.rates.XAG || 0) * usdToInr / GRAM_CONVERSION).toFixed(2);
            const platinumPerGram = ((data.rates.XPT || 0) * usdToInr / GRAM_CONVERSION).toFixed(2);

            return { gold: goldPerGram, silver: silverPerGram, platinum: platinumPerGram };
        } catch (err) {
            console.error("Error fetching metal prices:", err);
            return { gold: "--", silver: "--", platinum: "--" };
        }
    }

    // Update UI with animation
    async function updateMetalPrices() {
        const { gold, silver, platinum } = await fetchMetalPricesPerGram();

        const metals = [
            { el: goldEl, value: gold },
            { el: silverEl, value: silver },
            { el: platinumEl, value: platinum }
        ];

        metals.forEach(({ el, value }) => {
            if (!el) return;
            el.classList.add("updating");
            setTimeout(() => {
                el.textContent = `₹ ${value} / g`;
                el.classList.remove("updating");
            }, 200);
        });
    }

    // Initial load + refresh every 5 minutes
    updateMetalPrices();
    setInterval(updateMetalPrices, 5 * 60 * 1000);
});

// inquiry


fetch("http://localhost:8080/users/products/1/inquire", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({
    name: "John",
    phone: "9876543210",
    message: "Interested in this product"
  })
});