document.querySelectorAll('.retry-btn').forEach(button => {
  addRetryButtonListener(button)
});

document.querySelectorAll('.previous-btn').forEach(button => {
  const closestSection = button.closest('.section');
  button.addEventListener('htmx:afterRequest', function() {
    console.log('After Previous')
    const newDeleteButton = closestSection.querySelector('.res-delBtn');
    const newDeleteRetry = closestSection.querySelector('.retry-btn');
    addDeleteButtonListener(newDeleteButton)
    addRetryButtonListener(newDeleteRetry)
  });
});

function addRetryButtonListener(button){
  console.log('found Retry')
  console.log(button)
  const closestSection = button.closest('.section');
  button.addEventListener('htmx:afterRequest', function() {
    console.log('After Retry')
    const newDeleteButton = closestSection.querySelector('.res-delBtn');
    const newRetryButton = closestSection.querySelector('.retry-btn');
    addDeleteButtonListener(newDeleteButton)
    addRetryButtonListener(newRetryButton)
  });
}

document.querySelectorAll('.res-delBtn').forEach(button => {
  addDeleteButtonListener(button)
});

function addDeleteButtonListener(button) {
  console.log('found Delete')
  console.log(button)
  button.addEventListener('htmx:afterRequest', function () {
    const responseWrapper = button.closest('.response-wrapper');
    console.log('Delete button clicked.');
    const previousButton = responseWrapper.querySelector('.previous-btn');
    if (previousButton) {
      console.log("previous")
      previousButton.click();
      return;
    }
    const nextButton = responseWrapper.querySelector('.next-btn');
    if (nextButton) {
      console.log("next")
      nextButton.click();
      return;
    }
    console.log("regen")
    const retryButton = responseWrapper.querySelector('.retry-btn');
    retryButton.click();
  });
}