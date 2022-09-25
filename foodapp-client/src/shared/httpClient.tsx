import axios from 'axios';

const axiosInstance = axios.create({
    baseURL: 'http://localhost:8080',
    responseType: 'json',
    timeout: 180000,
});

axiosInstance.interceptors.request.use((config: any) => {
    const token = localStorage.getItem('token');
    config.headers.Authorization = token ? `Bearer ${token}` : '';
    return config;
});

const httpClient = {
    get: async (path: string, headers: Map<string, string> = new Map<string, string>()) => {
        // TO DO: Add optional headers
        return axiosInstance.get(path);
    },

    post: async (path: string, body: any, headers: Map<string, string> = new Map<string, string>()) => {
        // TO DO: Add optional headers
        return axiosInstance.post(path, body);
    },

    put: async (path: string, body: any, headers: Map<string, string> = new Map<string, string>()) => {
        // TO DO: Add optional headers
        return axiosInstance.put(path, body);
    },

    del: async (path: string, body: any, headers: Map<string, string> = new Map<string, string>()) => {
        // TO DO: Add optional headers
        return axiosInstance.delete(path, body);
    },
}

export default httpClient;