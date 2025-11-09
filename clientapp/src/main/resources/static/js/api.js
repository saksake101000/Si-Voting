/**
 * SI-VOTING - API Client Utility
 * Centralized API call handler with JWT authentication
 */

class APIClient {
    constructor(baseURL) {
        // Determine baseURL from (in order): explicit ctor arg, window.API_BASE global,
        // <meta name="api-base" content="..."> tag, or fallback to localhost for dev.
        const meta = typeof document !== 'undefined' ? document.querySelector('meta[name="api-base"]') : null;
        const globalBase = typeof window !== 'undefined' ? window.API_BASE : null;
        this.baseURL = baseURL || globalBase || (meta ? meta.content : null) || 'http://localhost:8080/api';
        this.token = this.getToken();
    }

    /**
     * Get JWT token from localStorage
     */
    getToken() {
        return localStorage.getItem('token') || sessionStorage.getItem('token');
    }

    /**
     * Set JWT token
     */
    setToken(token, remember = false) {
        if (remember) {
            localStorage.setItem('token', token);
        } else {
            sessionStorage.setItem('token', token);
        }
        this.token = token;
    }

    /**
     * Remove JWT token
     */
    removeToken() {
        localStorage.removeItem('token');
        sessionStorage.removeItem('token');
        this.token = null;
    }

    /**
     * Get default headers
     */
    getHeaders(isFormData = false) {
        const headers = {};
        
        if (!isFormData) {
            headers['Content-Type'] = 'application/json';
        }
        
        if (this.token) {
            headers['Authorization'] = `Bearer ${this.token}`;
        }
        
        return headers;
    }

    /**
     * Handle response
     */
    async handleResponse(response) {
        const contentType = response.headers.get('content-type');
        
        // Check if response is JSON
        if (contentType && contentType.includes('application/json')) {
            const data = await response.json();
            
            if (!response.ok) {
                throw {
                    status: response.status,
                    message: data.message || 'Request failed',
                    data: data
                };
            }
            
            return data;
        }
        
        // Handle non-JSON responses
        if (!response.ok) {
            throw {
                status: response.status,
                message: `HTTP ${response.status}: ${response.statusText}`,
                data: null
            };
        }
        
        return response;
    }

    /**
     * GET request
     */
    async get(endpoint, queryParams = {}) {
        try {
            const url = new URL(`${this.baseURL}${endpoint}`);
            Object.keys(queryParams).forEach(key => 
                url.searchParams.append(key, queryParams[key])
            );

            const response = await fetch(url.toString(), {
                method: 'GET',
                headers: this.getHeaders()
            });

            return await this.handleResponse(response);
        } catch (error) {
            console.error('GET Error:', error);
            throw error;
        }
    }

    /**
     * POST request
     */
    async post(endpoint, body = {}, isFormData = false) {
        try {
            const options = {
                method: 'POST',
                headers: this.getHeaders(isFormData)
            };

            if (isFormData) {
                options.body = body; // FormData object
            } else {
                options.body = JSON.stringify(body);
            }

            const response = await fetch(`${this.baseURL}${endpoint}`, options);
            return await this.handleResponse(response);
        } catch (error) {
            console.error('POST Error:', error);
            throw error;
        }
    }

    /**
     * PUT request
     */
    async put(endpoint, body = {}, isFormData = false) {
        try {
            const options = {
                method: 'PUT',
                headers: this.getHeaders(isFormData)
            };

            if (isFormData) {
                options.body = body;
            } else {
                options.body = JSON.stringify(body);
            }

            const response = await fetch(`${this.baseURL}${endpoint}`, options);
            return await this.handleResponse(response);
        } catch (error) {
            console.error('PUT Error:', error);
            throw error;
        }
    }

    /**
     * DELETE request
     */
    async delete(endpoint) {
        try {
            const response = await fetch(`${this.baseURL}${endpoint}`, {
                method: 'DELETE',
                headers: this.getHeaders()
            });

            return await this.handleResponse(response);
        } catch (error) {
            console.error('DELETE Error:', error);
            throw error;
        }
    }

    /**
     * Upload file
     */
    async uploadFile(endpoint, file, additionalData = {}) {
        try {
            const formData = new FormData();
            formData.append('file', file);
            
            // Add additional fields
            Object.keys(additionalData).forEach(key => {
                formData.append(key, additionalData[key]);
            });

            return await this.post(endpoint, formData, true);
        } catch (error) {
            console.error('Upload Error:', error);
            throw error;
        }
    }
}

/**
 * SI-VOTING API Endpoints
 */
class SIVotingAPI {
    constructor() {
        this.client = new APIClient();
    }

    // ========== Authentication ==========
    
    async login(email, password) {
        return await this.client.post('/auth/login', { email, password });
    }

    async register(userData) {
        return await this.client.post('/auth/register', userData);
    }

    async logout() {
        this.client.removeToken();
        return { success: true, message: 'Logged out successfully' };
    }

    async getCurrentUser() {
        return await this.client.get('/auth/me');
    }

    // ========== Events ==========
    
    async getEvents() {
        return await this.client.get('/events');
    }

    async getEventById(id) {
        return await this.client.get(`/events/${id}`);
    }

    async getEventByCode(code) {
        return await this.client.get(`/events/code/${code}`);
    }

    async createEvent(eventData) {
        return await this.client.post('/events', eventData);
    }

    async updateEvent(id, eventData) {
        return await this.client.put(`/events/${id}`, eventData);
    }

    async deleteEvent(id) {
        return await this.client.delete(`/events/${id}`);
    }

    async getMyEvents() {
        return await this.client.get('/events/my-events');
    }

    async getEventStatistics(id) {
        return await this.client.get(`/events/${id}/statistics`);
    }

    // ========== Candidates ==========
    
    async addCandidate(eventId, candidateData) {
        return await this.client.post(`/events/${eventId}/candidates`, candidateData);
    }

    async updateCandidate(eventId, candidateId, candidateData) {
        return await this.client.put(`/events/${eventId}/candidates/${candidateId}`, candidateData);
    }

    async deleteCandidate(eventId, candidateId) {
        return await this.client.delete(`/events/${eventId}/candidates/${candidateId}`);
    }

    // ========== Votes ==========
    
    async submitVote(candidateId, eventId) {
        return await this.client.post('/votes', { candidateId, eventId });
    }

    async checkIfVoted(eventId) {
        return await this.client.get(`/votes/check/${eventId}`);
    }

    // ========== Users ==========
    
    async getUserProfile() {
        return await this.client.get('/users/profile');
    }

    async updateUserProfile(profileData) {
        return await this.client.put('/users/profile', profileData);
    }

    // ========== Files ==========
    
    async uploadPhoto(file) {
        return await this.client.uploadFile('/files/upload', file);
    }
}

// Initialize global API instance
const api = new SIVotingAPI();

// Export for use in other scripts
window.APIClient = APIClient;
window.SIVotingAPI = SIVotingAPI;
window.api = api;
