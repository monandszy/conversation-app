let globalConversationId = 'null';

document.getElementById('dynamic-form').addEventListener('submit', function (event) {
  const formData = new FormData(this);
  event.preventDefault();
  if (globalConversationId === 'null') {
    new Promise(resolve => {
      htmx.ajax('POST', '/conversation', {
        swap: 'none',
        values: formData,
        handler: (_, r) => resolve(r.xhr)
      })
    }).then(xhr => {
      const data = JSON.parse(xhr.response);
      const conversationId = data.id;
      setGlobal(conversationId)
      // TODO add afterend logic
      htmx.ajax('GET', '/conversation/list', {target: '#sidebar-wrapper', swap: 'outerHTML'});
      htmx.ajax('GET', '/conversation/' + conversationId, {target: '#window-wrapper', swap: 'outerHTML'});
      window.history.pushState({}, '', '/conversation/' + conversationId);
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