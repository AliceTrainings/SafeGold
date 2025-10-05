// Get CSRF token from meta tags
const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

// Watchlist functionality
document.querySelectorAll('.wishlist-btn').forEach(button => {
    button.addEventListener('click', async function(e) {
        e.preventDefault();
        const productId = this.dataset.productId;
        const icon = this.querySelector('i');
        const isInWatchlist = icon.classList.contains('fas');
        
        try {
            const response = await fetch(`/api/watchlist/${productId}`, {
                method: isInWatchlist ? 'DELETE' : 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeader]: csrfToken
                }
            });
            
            if (response.ok) {
                // Toggle icon
                if (isInWatchlist) {
                    icon.classList.remove('fas', 'text-danger');
                    icon.classList.add('far');
                    showToast('Removed from watchlist', 'success');
                } else {
                    icon.classList.remove('far');
                    icon.classList.add('fas', 'text-danger');
                    showToast('Added to watchlist', 'success');
                }
            } else {
                showToast('Failed to update watchlist', 'error');
            }
        } catch (error) {
            console.error('Error:', error);
            showToast('An error occurred', 'error');
        }
    });
});

// Inquiry Modal functionality
document.querySelectorAll('.inquiry-btn').forEach(button => {
    button.addEventListener('click', function() {
        const productId = this.dataset.productId;
        const productName = this.dataset.productName;
        openInquiryModal(productId, productName);
    });
});

function openInquiryModal(productId, productName) {
    // Create modal dynamically
    const modalHTML = `
        <div class="modal fade" id="inquiryModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">
                            <i class="fas fa-question-circle me-2"></i>Inquiry for ${productName}
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form id="inquiryForm">
                            <input type="hidden" id="productId" value="${productId}">
                            
                            <div class="mb-3">
                                <label for="message" class="form-label">Your Message *</label>
                                <textarea class="form-control" id="message" rows="4" 
                                          placeholder="Please describe your inquiry..." required></textarea>
                            </div>
                            
                            <div class="mb-3">
                                <label for="contactNumber" class="form-label">Contact Number *</label>
                                <input type="tel" class="form-control" id="contactNumber" 
                                       placeholder="+91 9876543210" required>
                            </div>
                            
                            <div class="alert alert-info">
                                <i class="fas fa-info-circle me-2"></i>
                                Our team will contact you within 24 hours.
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="button" class="btn btn-primary" onclick="submitInquiry()">
                            <i class="fas fa-paper-plane me-2"></i>Submit Inquiry
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    // Remove existing modal if any
    const existingModal = document.getElementById('inquiryModal');
    if (existingModal) {
        existingModal.remove();
    }
    
    // Add modal to body
    document.body.insertAdjacentHTML('beforeend', modalHTML);
    
    // Show modal
    const modal = new bootstrap.Modal(document.getElementById('inquiryModal'));
    modal.show();
}

async function submitInquiry() {
    const productId = document.getElementById('productId').value;
    const message = document.getElementById('message').value;
    const contactNumber = document.getElementById('contactNumber').value;
    
    if (!message || !contactNumber) {
        showToast('Please fill in all required fields', 'error');
        return;
    }
    
    try {
        const response = await fetch('/api/inquiries', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify({
                productId: productId,
                message: message,
                contactNumber: contactNumber
            })
        });
        
        if (response.ok) {
            showToast('Inquiry submitted successfully!', 'success');
            bootstrap.Modal.getInstance(document.getElementById('inquiryModal')).hide();
            document.getElementById('inquiryForm').reset();
        } else {
            showToast('Failed to submit inquiry', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showToast('An error occurred', 'error');
    }
}

// Toast notification function
function showToast(message, type = 'success') {
    const toastHTML = `
        <div class="toast align-items-center text-white bg-${type === 'success' ? 'success' : 'danger'} border-0" 
             role="alert" style="position: fixed; top: 20px; right: 20px; z-index: 9999;">
            <div class="d-flex">
                <div class="toast-body">
                    <i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-circle'} me-2"></i>
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
    `;
    
    document.body.insertAdjacentHTML('beforeend', toastHTML);
    const toastElement = document.querySelector('.toast:last-child');
    const toast = new bootstrap.Toast(toastElement);
    toast.show();
    
    // Remove toast after it's hidden
    toastElement.addEventListener('hidden.bs.toast', function() {
        this.remove();
    });
}

// Product image hover effect
document.querySelectorAll('.product-image').forEach(img => {
    img.addEventListener('mouseenter', function() {
        this.style.transform = 'scale(1.05)';
        this.style.transition = 'transform 0.3s ease';
    });
    
    img.addEventListener('mouseleave', function() {
        this.style.transform = 'scale(1)';
    });
});

// Auto-submit filter form on select change
document.querySelectorAll('#filterForm select').forEach(select => {
    select.addEventListener('change', function() {
        document.getElementById('filterForm').submit();
    });
});