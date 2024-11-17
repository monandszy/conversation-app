if (typeof globalConversationId === 'undefined') {
  let globalConversationId = 'null';
}
document.getElementById('dynamic-form').addEventListener('submit', function (event) {
  const formData = new FormData(this);
  event.preventDefault();
  const container = document.querySelector("#window-content");
  container.scrollTop = container.scrollHeight;
  if (globalConversationId === 'null') {
    // begin conversation
    htmx.ajax('POST', '/conversation', {
      swap: 'beforebegin',
      target: '#sidebar-content',
      values: formData,
    }).then(() => {
      document.getElementById('generate-form-button').innerText = 'Post_Generate';
      htmx.ajax('GET', '/conversation/' + globalConversationId, {target: '#window-wrapper', swap: 'outerHTML'});
      window.history.pushState({}, '', '/conversation/' + globalConversationId);
    });
  } else {
    htmx.ajax('POST', '/conversation/' + globalConversationId,
      {values: formData, swap: 'beforeend', target: '#window-content'})
      .then(() => {
        container.scrollTop = container.scrollHeight;
        incrementCount('header-section-count');
        incrementCount('header-request-count');
        incrementCount('header-response-count');
      });
  }
});

function setRequestPostAfter(button) {
  button.addEventListener('htmx:afterRequest', function () {
    incrementCount('header-request-count')
    incrementCount('header-response-count')
  })
}

function setResponsePostAfter(button) {
  button.addEventListener('htmx:afterRequest', function () {
    incrementCount('header-response-count')
  })
}

function resetGlobal() {
  globalConversationId = 'null'
  document.getElementById('generate-form-button').innerText = 'Begin_Generate';
}

function setGlobal(conversationId) {
  globalConversationId = conversationId
  document.getElementById('generate-form-button').innerText = 'Post_Generate';
}

function setConversationDeleteAfter(event, button) {
  button.addEventListener('htmx:afterRequest', function () {
    const selectedButton = document.querySelector('#selected-conv-DelBtn')
    const clickedDeletePath = button.getAttribute('hx-delete');
    const selectedDeletePath = selectedButton.getAttribute('hx-delete');
    if (clickedDeletePath === selectedDeletePath) {
      button.addEventListener('htmx:afterRequest', function () {
        const newConversationForm = document.querySelector('#new-conversation-form-button');
        newConversationForm.click();
      })
    }
  });
}

function setSelectedDeleteAfter(button) {
  const selector = '#sidebar-content .sidebar-item .conv-delBtn[hx-delete="' + button.getAttribute('hx-delete') + '"]'
  const pairButton = document.querySelector(selector);
  if (pairButton)
    pairButton.closest('.sidebar-item').remove();
  button.addEventListener('htmx:afterRequest', function () {
    const newConversationForm = document.querySelector('#new-conversation-form-button');
    newConversationForm.click();
  })
}