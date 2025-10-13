const API_KEY = "goldapi-5z18ld4gkwkcye86-io"; // your real API key
const API_URL = "https://www.goldapi.io/api/XAU/INR"; // Fetch gold in INR

async function fetchMetalPrices() {
  try {
    const metals = ["XAU", "XAG", "XPT"]; // Gold, Silver, Platinum

    for (const metal of metals) {
      const response = await fetch(`https://www.goldapi.io/api/${metal}/INR`, {
        headers: {
          "x-access-token": API_KEY,
          "Content-Type": "application/json"
        }
      });

      if (!response.ok) throw new Error("API Error " + response.status);
      const data = await response.json();

      if (metal === "XAU") document.getElementById("gold-price").textContent = `₹${data.price}`;
      if (metal === "XAG") document.getElementById("silver-price").textContent = `₹${data.price}`;
      if (metal === "XPT") document.getElementById("platinum-price").textContent = `₹${data.price}`;
    }
  } catch (err) {
    console.error("Error fetching metal prices:", err);
  }
}

// Initial fetch
fetchMetalPrices();
// Refresh every 5 minutes
setInterval(fetchMetalPrices, 300000);


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
