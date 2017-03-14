//  Local functions
//var URL = "localhost:8083"
// Russian date picker


$( function() {

   $("#at-date").datepicker(
   {
       dateFormat: "yy-mm-dd",
       monthNames: ["Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"],
       monthNamesShort: ["Янв", "Фев", "Мар", "Апр", "Май", "Июн", "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек"],
       dayNames: ["Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"],
       dayNamesMin: ["Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб"]
   }
   );

  // Here are click events on all buttons
  // and make an AJAX request to the backend
   $("#btn-read").click(function(event) {

       window.confirm("Получить данные из SAP за " + $("#at-date").val() + "?");
       var params = "at-date=" + $("#at-date").val() + "&" + "btn-read=" + $("#btn-read").val();
       var xhttp = new XMLHttpRequest();
       xhttp.open("POST", "/cj", true);
       xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
       xhttp.onreadystatechange = function() {
           if (this.readyState == 4 && this.status == 200) {
           } else {
               alert("Ошибка соединения с сервером");
           }
       };
       xhttp.send(params);

   });

   // Click on the button (BTN-HISTORY)
   // and get data from database
   $("#btn-history").click(function(event) {

       window.confirm("Просмотреть исторические данные за " + $("#at-date").val() + "?");
       var params = "at-date=" + $("#at-date").val() + "&" + "btn-history=" + $("#btn-history").val();
       var xhttp = new XMLHttpRequest();
       xhttp.open("POST", "/cj", true);
       xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
       xhttp.onreadystatechange = function() {
           if (this.readyState == 4 && this.status == 200) {
           } else {
               alert("Ошибка соединения с сервером");
           }
        };
       xhttp.send(params);

   });

});
