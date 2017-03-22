//  Local functions
//var URL = "localhost:8083"
// Russian date picker


$( function() {

   $("#atDate").datepicker(
   {
       dateFormat: "dd-mm-yy",
       monthNames: ["Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"],
       monthNamesShort: ["Янв", "Фев", "Мар", "Апр", "Май", "Июн", "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек"],
       dayNames: ["Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"],
       dayNamesMin: ["Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб"]
   }
   );

  // Here are click events on all buttons
  // and make an AJAX request to the backend
  // Show table element
   // After all events

});