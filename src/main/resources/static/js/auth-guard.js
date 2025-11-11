function getAuthData() {
    return JSON.parse(localStorage.getItem("authData") || "{}");
}

function getToken() {
    const authData = getAuthData();
    return authData.token || null;
}

function getRole() {
    const authData = getAuthData();
    return authData.role || null;
}

const token = getToken(); // pega o token real dentro de authData

if (!token) {
  window.location.href = "/login.html";
}
