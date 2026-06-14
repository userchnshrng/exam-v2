import http from './http';
export function login(data) {
    return http.post('/api/login', data);
}
//# sourceMappingURL=auth.js.map