let globalConversationId = 'null';

document.getElementById('dynamic-form').addEventListener('submit', function (event) {
  const formData = new FormData(this);
  event.preventDefault();
  if (globalConversationId === 'null') {
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
  globalConversationId = 'null'
  document.getElementById('submit-button').innerText = 'Begin_Generate';
}

function setGlobal(conversationId) {
  globalConversationId = conversationId
  document.getElementById('submit-button').innerText = 'Post_Generate';
}