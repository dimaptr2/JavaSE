// Function, events

$( function() {

    var headTable = "<table id=\"main-table\" class=\"responstable\"\> \
      <tr> \
      <th>№</th> \
      <th class=\"width-100px\">Приходный ордер</th> \
      <th>Краткое описание</th> \
      <th class=\"width-100px\">Дата проводки</th> \
      <th class=\"width-45px\">Поставка</th> \
      <th>Сумма</th> \
      </tr> \
      <tbody id=\"body-area\"> \
      </tbody> \
      </table> \
      ";

// positions of receipt order
    var collection;

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

      $("#middle").html(headTable);
      var dt = $("#atDate").val();
      if (dt === null) {
          alert("Выберите число");
          return;
      }
      var btnVal = $("#btnRead").val();

      // POST request to the servlet with parameters
      $.post("/cj", {"atDate": dt, "btnRead": btnVal}, function(data) {

          var keys = ["postingNumber", "positionText", "postingDate", "deliveryId", "amount"];
          var result = JSON.stringify(data, keys);
          collection = JSON.parse(result);
          if (collection.length > 0) {
              for (var i = 0; i < collection.length; i++) {
                  var counter = i + 1;
                  $("#body-area").append("<tr>");
                  $("#body-area").append("<td>" + counter + "</td>");
                  var postNumber = collection[i].postingNumber;
                  var delId = collection[i].deliveryId;
                  var req = "/items?pNumb=" + postNumber + "&delId=" + delId;
                  $("#body-area").append("<td><a href=\"" + req + "\">"
                  + postNumber + "</a></td>");
                  $("#body-area").append("<td>" + collection[i].positionText + "</td>");
                  var posDate = getHumanableDateFormat(collection[i].postingDate);
                  $("#body-area").append("<td>" + posDate + "</td>");
                  // build links for deliveries
                  $("#body-area").append("<td>" + delId + "</td>");
                  var money = getMoney(collection[i].amount);
                  $("#body-area").append("<td>" + money + "</td>");
                  $("#body-area").append("</tr>");
              }
          } // collection

      }); // post

       // inverse date
       function getHumanableDateFormat(value) {
           var txtDate = value.toString();
           var temp = txtDate.split("-");
           txtDate = temp[2] + "-" + temp[1] + "-" + temp[0];
           return txtDate;
       }

       // transformation floating point number in the currency form
       function getMoney(value) {
             var textValue = value.toString();
             var temp = textValue.split(".");
             textValue = "";
             var rub = temp[0];
             var cop = temp[1];
             if (rub ===  undefined) {
                 rub = "0";
             }
             if (cop ===  undefined) {
                 cop = "00";
             }
             textValue = rub + "р." + " " + cop + "к.";
             return textValue;
       }

    }); // button read


// Read receipts from database
   $("#btnHistory").click(function(event) {
      $("#main-table").remove();
   });

   $("#btnPrint").click(function(event) {

   });

// Print the Z-report
   $("#btnZ").click(function(event) {
      $("#main-table").remove();
      var now = new Date();
   });

   $("#btnClean").click(function(event) {
      $("#main-table").remove();
   });



}); // end of all JQuery functions in the page index.html