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
    //   $("#main-table").remove();
      var headTable = "<table id=\"main-table\" class=\"responstable\"\> \
      <tr> \
      <th>№ ордера</th><th>Краткое описание</th><th>Дата проводки</th> \
      <th>Поставка</th><th>Сумма</th> \
      </tr> \
      </table>";
      $("#middle").html(headTable);

      var dt = $("#atDate").val();
      var btnVal = $("#btnRead").val();
      // POST request to the servlet with parameters
      $.post("/cj", {"atDate": dt, "btnRead": btnVal}, function(data) {
          if(!data) {
              for (var i = 0; i < data.length; i++) {
                  var answer = $.parseJSON(data[i]);
                  $("#main-table").append("<tr>");
                  $("#main-table").append("<td>" + answer.postingNumber + "</td>");
                  $("#main-table").append("<td>" + answer.positionText + "</td>");
                  $("#main-table").append("<td>" + answer.postingDate + "</td>");
                  $("#main-table").append("<td>" + answer.deliveryId + "</td>");
                  $("#main-table").append("<td>" + answer.amount + "</td>");
                  $("#main-table").append("</tr>");
              }
          } else {
              alert("failure");
          }
      });
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

});

//          $("#middle").append("<#list result as row>\n");
//          $("#middle").append("<tr>\n");
//          $("#middle").append("<td>${row.postingNumber}</td>\n");
//          $("#middle").append("<td>${row.positionText}</td>\n");
//          $("#middle").append("<td>${row.postingDate}</td>\n");
//          $("#middle").append("<td>${row.deliveryId}</td>\n");
//          $("#middle").append("<td>${row.amount}</td>\n");
//          $("#middle").append("</tr>\n");
//          $("#middle").append("<#/list>\n");