document.addEventListener("DOMContentLoaded", () => {
  const deleteButtons = document.querySelectorAll(".btn-delete");

  deleteButtons.forEach(btn => {
    btn.addEventListener("click", () => {
      if (confirm("Tem certeza que deseja excluir este pedido?")) {
        btn.closest(".pedido-card").remove();
      }
    });
  });

  const tags = document.querySelectorAll(".tag");
  tags.forEach(tag => {
    tag.addEventListener("click", () => {
      tag.remove(); // remove filtro clicado
    });
  });
});
