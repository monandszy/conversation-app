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
      const newSidebarItem = document
        .querySelector('#sidebar-content .sidebar-item:last-child');
      const newDeleteButton = newSidebarItem.querySelector('.delete-btn');
      addDeleteButtonListener(newDeleteButton);
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

document.querySelectorAll('.delete-btn').forEach(button => {
  addDeleteButtonListener(button)
});

function addDeleteButtonListener(button) {
  button.addEventListener('click', function () {
    const deletedConversationId = button.getAttribute('data-conversation-id');
    if (deletedConversationId === globalConversationId) {
      // If the conversation ID matches the global conversation ID, send the additional request
      resetGlobal()
      window.history.pushState({}, '', '/conversation');
      htmx.ajax('GET', '/conversation/introduction', {
        target: '#window-wrapper',
        swap: 'outerHTML',
      });
    }
  });
}