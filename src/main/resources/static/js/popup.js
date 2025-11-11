function showPopup(response) {
  const container = document.getElementById("popup-container");

  // Define o tipo de popup baseado no status
  let type = "success";
  if (response.status >= 400 && response.status < 500) type = "warning";
  else if (response.status >= 500) type = "error";

  // Cria o popup
  const popup = document.createElement("div");
  popup.classList.add("popup", type);

  // Define a mensagem
  popup.textContent = response.message || "Ocorreu um erro inesperado.";

  // Adiciona no container
  container.appendChild(popup);

  // Remove após 5 segundos
  setTimeout(() => {
    popup.style.opacity = "0";
    popup.style.transform = "translateY(-10px)";
    setTimeout(() => popup.remove(), 300);
  }, 5000);
}
