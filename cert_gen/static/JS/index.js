function getCookie(name) {
    let cookieValue = null;
    if (document.cookie && document.cookie !== "") {
      const cookies = document.cookie.split(";");
      for (let i = 0; i < cookies.length; i++) {
        const cookie = cookies[i].trim();
        // Does this cookie string begin with the name we want?
        if (cookie.substring(0, name.length + 1) === (name + "=")) {
          cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
          break;
        }
      }
    }
    return cookieValue;
  }

// function save_data(){
//     const inp_name = document.querySelector('#name');
//     const inp_cource = document.querySelector('#course');
//     obj.name = inp_name.value;
//     obj.course = inp_cource.value;
//     indicator = document.querySelector('#indicator');
//     indicator.innerHTML = 'Please wait!';
//
//     fetch('generating_file', {
//         method: 'POST',
//         credentials: "same-origin",
//         headers: {
//             "X-Requested-With": "XMLHttpRequest",
//             "X-CSRFToken": getCookie("csrftoken"),
//         },
//         body: JSON.stringify(obj)
//     })
//     .then(response => response.json())
//     .then(data => {
//         document.location = '';
//         indicator.innerHTML = '';
//     });
// }