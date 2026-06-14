import { defineStore } from 'pinia';
const storageKey = 'exam-v2-user';
function readUser() {
    const raw = localStorage.getItem(storageKey);
    if (!raw)
        return null;
    return JSON.parse(raw);
}
export const useUserStore = defineStore('user', {
    state: () => ({
        user: readUser()
    }),
    getters: {
        role: (state) => state.user?.role || '',
        isLoggedIn: (state) => Boolean(state.user)
    },
    actions: {
        setUser(user) {
            this.user = user;
            localStorage.setItem(storageKey, JSON.stringify(user));
        },
        clearUser() {
            this.user = null;
            localStorage.removeItem(storageKey);
        }
    }
});
//# sourceMappingURL=user.js.map