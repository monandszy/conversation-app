let globalConversationId = 'null';

document.getElementById('dynamic-form').addEventListener('submit', function (event) {
  const formData = new FormData(this);
  event.preventDefault();
  if (globalConversationId === 'null') {
    // begin conversation
    htmx.ajax('POST', '/conversation', {
      swap: 'beforeend',
      target: '#sidebar-content',
      values: formData,
    }).then(() => {
      document.getElementById('submit-button').innerText = 'Post_Generate';
      htmx.ajax('GET', '/conversation/' + globalConversationId, {target: '#window-wrapper', swap: 'outerHTML'});
      window.history.pushState({}, '', '/conversation/' + globalConversationId);
    });
  } else {
    htmx.ajax('POST', '/conversation/' + globalConversationId,
      {values: formData, swap: 'beforeend', target: '#window-content'});
  }
});

function resetGlobal() {
  const element = document.querySelector('#selected-sidebar-item');
  if (element) {
    element.removeAttribute('id');
  }
  globalConversationId = 'null'
  document.getElementById('submit-button').innerText = 'Begin_Generate';
}

function setGlobal(conversationId, button) {
  globalConversationId = conversationId
  const element = document.querySelector('#selected-sidebar-item');
  if (element) {
    element.removeAttribute('id');
  }
  const selected = button.closest(".sidebar-item");
  selected.id = 'selected-sidebar-item'
  document.getElementById('submit-button').innerText = 'Post_Generate';
}

function setConversationDeleteAfter(event, button) {
  button.addEventListener('htmx:afterRequest', function () {
    if (button.closest(".sidebar-item").id === 'selected-sidebar-item') {
      const newConversationForm = document.querySelector('#new-conversation-form-button');
      newConversationForm.click();
    }
  });
}