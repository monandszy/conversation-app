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
  const nextButton = responseWrapper.querySelector('.next-btn');
  if (previousButton) {
    button.addEventListener('htmx:afterRequest', function () {
      previousButton.click();
    })
  } else if (nextButton) {
    button.addEventListener('htmx:afterRequest', function () {
      nextButton.click();
    });
  }
  const retryButton = responseWrapper.querySelector('.retry-btn');
  button.addEventListener('htmx:afterRequest', function () {
    retryButton.click();
  });
  decrementCount('header-response-count')
}

function setRequestDeleteAfter(event, button) {
  const requestWrapper = button.closest('.request-wrapper');
  const previousButton = requestWrapper.querySelector('.previous-btn:not(.nested)');
  const nextButton = requestWrapper.querySelector('.next-btn:not(.nested)');
  if (previousButton) {
    button.addEventListener('htmx:afterRequest', function () {
      previousButton.click();
      updateCount()
    })
  } else if (nextButton) {
    button.addEventListener('htmx:afterRequest', function () {
      nextButton.click();
      updateCount()
    })
  }
  event.preventDefault();
  const section = button.closest('.section');
  const deleteButton = section.querySelector(".section-delBtn");
  deleteButton.click()
}

function setSectionDeleteAfter(event, button) {
  const windowContent = document.querySelector("#window-wrapper");
  const sections = windowContent.querySelectorAll(".section");
  if (sections.length === 1) {
    event.preventDefault();
    const deleteButton = document.querySelector('#selected-conv-DelBtn');
    deleteButton.click();
    return;
  }
  button.addEventListener('htmx:afterRequest', function () {
    updateCount()
  })
}

function incrementCount(countId) {
  const countElement = document.getElementById(countId);
  let currentCount = parseInt(countElement.innerText);
  currentCount++;
  countElement.innerText = '' + currentCount;
}

function decrementCount(countId) {
  const countElement = document.getElementById(countId);
  let currentCount = parseInt(countElement.innerText);
  currentCount--;
  countElement.innerText = '' + currentCount;
}

function updateCount() {
  htmx.ajax('GET', '/conversation/' + globalConversationId + '/header', {
    target: '#window-header',
    swap: 'outerHTML'
  });
}

function correctPreviousScroll(button) {
  const container = document.querySelector("#window-content")
  const scrollPositionBefore = container.scrollTop;
  button.addEventListener('htmx:afterRequest', function () {
    container.scrollTop = scrollPositionBefore;
  })
}

function correctNextScroll(button) {
  const container = document.querySelector("#window-content")
  const scrollPositionBefore = container.scrollHeight;
  button.addEventListener('htmx:afterRequest', function () {
    container.scrollTop = container.scrollHeight - scrollPositionBefore;
  })
}