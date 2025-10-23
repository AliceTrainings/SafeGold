// ====== Metal Prices Fetch & Animation ======
const API_KEY = "c45bbc35b2742eacfc58e298bc1fd3b6"; // Your API key
const API_URL = "https://api.metalpriceapi.com/v1/latest";
const GRAM_CONVERSION = 31.1035; // 1 troy ounce = 31.1035 grams

// DOM Elements
const goldEl = document.getElementById("gold-price");
const silverEl = document.getElementById("silver-price");
const platinumEl = document.getElementById("platinum-price");

// Fetch metal prices from API
async function fetchMetalPricesPerGram() {
  try {
    // Base: XAU (Gold), get XAG (Silver), XPT (Platinum) rates in INR
    const response = await fetch(
      `${API_URL}?api_key=${API_KEY}&base=XAU&currencies=XAG,XPT,INR`
    );
    if (!response.ok) throw new Error("API fetch failed");

    const data = await response.json();

    // Convert troy ounce price → price per gram
    const goldPerGram = (data.rates.INR / GRAM_CONVERSION).toFixed(2);
    const silverPerGram = (data.rates.INR / (data.rates.XAG || 1) / GRAM_CONVERSION).toFixed(2);
    const platinumPerGram = (data.rates.INR / (data.rates.XPT || 1) / GRAM_CONVERSION).toFixed(2);

    return { gold: goldPerGram, silver: silverPerGram, platinum: platinumPerGram };
  } catch (err) {
    console.error("Error fetching metal prices:", err);
    return { gold: "--", silver: "--", platinum: "--" };
  }
}

// Animate & update UI
async function updateMetalPrices() {
  const { gold, silver, platinum } = await fetchMetalPricesPerGram();

  const metals = [
    { element: goldEl, value: gold },
    { element: silverEl, value: silver },
    { element: platinumEl, value: platinum },
  ];

  metals.forEach(({ element, value }) => {
    element.classList.add("updating");
    setTimeout(() => {
      element.textContent = `₹ ${value} / g`;
      element.classList.remove("updating");
    }, 300);
  });
}

// Initial load + auto-refresh every 5 min
updateMetalPrices();
setInterval(updateMetalPrices, 5 * 60 * 1000);


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