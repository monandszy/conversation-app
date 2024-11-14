if (typeof globalConversationId === 'undefined') {
  let globalConversationId = 'null';
}
document.getElementById('dynamic-form').addEventListener('submit', function (event) {
  const formData = new FormData(this);
  event.preventDefault();
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
  console.log('reset')
  console.log(globalConversationId)
  globalConversationId = 'null'
  document.getElementById('generate-form-button').innerText = 'Begin_Generate';
}

function setGlobal(conversationId) {
  console.log('set')
  console.log(conversationId)
  globalConversationId = conversationId
  document.getElementById('generate-form-button').innerText = 'Post_Generate';
}

function setConversationDeleteAfter(event, button) {
  button.addEventListener('htmx:afterRequest', function () {
    const selectedButton = document.querySelector('selected-conv-DelBtn')
    const clickedDeletePath = button.getAttribute('hx-delete');
    const selectedDeletePath = selectedButton.getAttribute('hx-delete');
    if (clickedDeletePath === selectedDeletePath) {
      redirectToIntroduction()
    }
  });
}
function redirectToIntroduction() {
  const newConversationForm = document.querySelector('#new-conversation-form-button');
  newConversationForm.click();
}