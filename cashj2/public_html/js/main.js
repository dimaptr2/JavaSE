// Function, events

$( function() {

   $("#atDate").datepicker(
       {
               dateFormat: "dd-mm-yy",
               monthNames: ["Январь", "Февраль", "Март", "Апрель", 
               "Май", "Июнь", "Июль", "Август", 
               "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"],
               monthNamesShort: ["Янв", "Фев", "Мар", "Апр", "Май", "Июн", "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек"],
               dayNames: ["Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"],
               dayNamesMin: ["Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб"]
        }
   );

//    click in the buttons
// Read data from SAP
   $("#btnRead").click(function(event) {

      var headTable = "<table id=\"main-table\" class=\"responstable\"\> \
      <tr> \
      <th>№ п/п</th> \
      <th>Приходный ордер</th><th>Краткое описание</th><th>Дата проводки</th> \
      <th>Поставка</th><th>Сумма</th> \
      <th>s</th> \
      </tr> \
      <tbody id=\"body-area\"> \
      </tbody> \
      </table>";
      $("#middle").html(headTable);

      var dt = $("#atDate").val();
      var btnVal = $("#btnRead").val();
      // POST request to the servlet with parameters
      $.post("/cj", {"atDate": dt, "btnRead": btnVal}, function(data) {
          var keys = ["postingNumber", "positionText", "postingDate", "deliveryId", "amount"];
          var result = JSON.stringify(data, keys);
          var collection = JSON.parse(result);
          if (collection.length > 0) {
              for (var i = 0; i < collection.length; i++) {
                  var counter = i + 1;
                  $("#body-area").append("<tr>");
                  $("#body-area").append("<td>" + counter + "</td>");
                  $("#body-area").append("<td>" + collection[i].postingNumber + "</td>");
                  $("#body-area").append("<td>" + collection[i].positionText + "</td>");
                  $("#body-area").append("<td>" + collection[i].postingDate + "</td>");
                  // build links for deliveries
                  $("#body-area").append("<td>" + collection[i].deliveryId + "</td>");
                  $("#body-area").append("<td>" + collection[i].amount + "</td>");
                  $("#body-area").append("<td><input type=\"checkbox\" class=\"checked\"></td>")
                  $("#body-area").append("</tr>");
                }
          }
      });

    }); // button read

    //   Click for printing values, that could were selected
    $("#btnPrint").click(function(event) {
//       var table = $("#main-table");
       alert("Click!");
    });

// Read receipts from database
   $("#btnHistory").click(function(event) {
      $("#main-table").remove();
   });

// Print the Z-report
   $("#btnZ").click(function(event) {
      $("#main-table").remove();
   });

   $("#btnClean").click(function(event) {
         $("#main-table").remove();
   });

}); // end of all JQuery functions in the page index.html