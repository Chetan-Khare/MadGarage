// Handle image preview when user selects a file
document.getElementById('imageUpload').addEventListener('change', function(event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            const preview = document.getElementById('imagePreview');
            preview.src = e.target.result;
            preview.style.display = 'block';
        }
        reader.readAsDataURL(file);
    }
});

async function sendMessage() {
    const inputField = document.getElementById('userInput');
    const fileField = document.getElementById('imageUpload');
    const chatBox = document.getElementById('chatBox');
    const messageText = inputField.value.trim();
    const imageFile = fileField.files[0];

    if (!messageText && !imageFile) return;

    // 1. Display User Message in Chat UI
    let userHtml = `<div class="message user-message">${messageText}`;
    if (imageFile) {
        userHtml += `<br><img src="${document.getElementById('imagePreview').src}" class="uploaded-img">`;
    }
    userHtml += `</div>`;
    chatBox.innerHTML += userHtml;
    chatBox.scrollTop = chatBox.scrollHeight;

    // 2. Prepare Data to Send to Spring Boot
    const formData = new FormData();
    formData.append("message", messageText);
    if (imageFile) {
        formData.append("image", imageFile);
    }

    // 3. Clear inputs and preview
    inputField.value = '';
    fileField.value = '';
    document.getElementById('imagePreview').style.display = 'none';

    // Add a temporary loading message
    const loadingId = "loading-" + Date.now();
    chatBox.innerHTML += `<div id="${loadingId}" class="message ai-message"><i>Mechanic is inspecting your vehicle...</i></div>`;
    chatBox.scrollTop = chatBox.scrollHeight;

    try {
        // 4. Send to Spring Boot Controller
        const response = await fetch('/api/assistant/chat', {
            method: 'POST',
            body: formData
        });

        // Parse the response as JSON (an array of Products)
        const products = await response.json();

        // Remove the loading message
        document.getElementById(loadingId).remove();

        // 5. Build the UI based on the database results
        let aiHtml = `<div class="message ai-message">`;

        if (products.length === 0) {
            aiHtml += `I found your vehicle, but we currently don't have any parts matching that request in stock.`;
        } else {
            aiHtml += `Here are the guaranteed-fit parts I found for your vehicle:<br><br>`;

            // Loop through the JSON array and build a Bootstrap card for each product
            products.forEach(product => {
                aiHtml += `
                <div class="card mb-2 shadow-sm border-0" style="max-width: 250px;">
                    <div class="card-body p-3">
                        <h6 class="card-title mb-1 fw-bold">${product.brand}</h6>
                        <p class="card-text small mb-2 text-muted">${product.partName} <br> SKU: ${product.sku}</p>
                        <div class="d-flex justify-content-between align-items-center mt-2">
                            <span class="fw-bold text-success">₹${product.price || 'N/A'}</span>
                            <button class="btn btn-sm btn-outline-primary" onclick="addToCart(${product.id})">Add to Cart</button>
                        </div>
                    </div>
                </div>`;
            });
        }

        aiHtml += `</div>`;
        chatBox.innerHTML += aiHtml;
        chatBox.scrollTop = chatBox.scrollHeight;

    } catch (error) {
        document.getElementById(loadingId).remove();
        chatBox.innerHTML += `<div class="message ai-message text-danger">Connection error. Please ensure the server is running.</div>`;
        console.error("Chat error:", error);
    }
}

// Placeholder for the shopping cart logic
function addToCart(productId) {
    alert("Added product ID " + productId + " to your cart!");
}