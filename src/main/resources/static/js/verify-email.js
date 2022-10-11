async function autoClose() {
  let counter = 3;
  let paragraph = document.getElementById("page-close-counter");

  function count() {
    return new Promise(resolve => {
      setTimeout(() => {
        --counter;
        resolve();
      }, 1000);
    });
  }

  while (counter !== 0) {
    paragraph.innerText = `현재 페이지는 ${counter}초 후 자동으로 닫힙니다.`;
    await count();
  }

  paragraph.innerText = `페이지가 계속 표시되면 직접 닫아주세요.`;
  window.close();
}

autoClose();