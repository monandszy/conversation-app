// Prevent "Enter" from creating a new line in any editable paragraph
document.addEventListener("keydown", function (event) {
  if (event.target.classList.contains("request") && event.key === "Enter") {
    if (event.ctrlKey) {
      event.preventDefault();
      event.target.blur()
    }
  }
});

function updateHiddenInput(paragraph) {
  let hiddenInput = paragraph.nextElementSibling;
  hiddenInput.value = paragraph.innerText;
}

function setResponseDeleteAfter(button) {
  const responseWrapper = button.closest('.response-wrapper');
  const previousButton = responseWrapper.querySelector('.previous-btn');
  if (previousButton) {
    button.addEventListener('htmx:afterRequest', function () {
      previousButton.click();
    })
    return;
  }
  const nextButton = responseWrapper.querySelector('.next-btn');
  if (nextButton) {
    button.addEventListener('htmx:afterRequest', function () {
      nextButton.click();
    });
    return;
  }
  const retryButton = responseWrapper.querySelector('.retry-btn');
  button.addEventListener('htmx:afterRequest', function () {
    retryButton.click();
  });
}

function setRequestDeleteAfter(event, button) {
  const responseWrapper = button.closest('.request-wrapper');
  const previousButton = responseWrapper.querySelector('.previous-btn');
  if (previousButton) {
    button.addEventListener('htmx:afterRequest', function () {
      previousButton.click();
    })
    return;
  }
  const nextButton = responseWrapper.querySelector('.next-btn');
  if (nextButton) {
    button.addEventListener('htmx:afterRequest', function () {
      nextButton.click();
    })
    return;
  }
  if (event) {
    event.preventDefault();
    const section = button.closest('.section');
    const deleteButton = section.querySelector(".section-delBtn");
    deleteButton.click()
  }
}

function setSectionDeleteAfter(event, button) {
  const windowContent = document.querySelector("#window-content");
  const sections = windowContent.querySelectorAll(".section");
  if (sections.length === 1) {
    event.preventDefault();
    const element = document.querySelector('#selected-sidebar-item');
    const deleteButton = element.querySelector('.delete-btn');
    deleteButton.click();
  }
}