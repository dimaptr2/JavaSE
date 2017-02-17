// My functions

// Build a table header
function buildHeader() {

    var header =  "<tr id=\"table-header\">" +
      "<th class=\"<th class=\"w3-border\">Id</th><th class=\"w3-border\">w3-border\">Номер чека</th>" +
      "<th class=\"w3-border\">Кассовая книга</th><th class=\"w3-border\">Балансовая единица</th>" +
      "<th class=\"w3-border\">Финансовый год</th><th class=\"w3-border\">Дата проводки</th>" +
      "<th class=\"w3-border\">Номер поставки</th><th class=\"w3-border\">Описание</th>";

      return header;
}

// Connecton error
function getConnectionError() {
    return "<p id=\"connection\" class=\"w3-margin w3-center\"><font class=\"w3-red\">Нет связи с сервером</font></p>";
}


// Get data and build a table
function getDataFromSAP() {

    var xhttp = new XMLHttpRequest();

//    xhttp.onreadystatechange = function() {
//        if (this.readyState == 4 && this.status == 200) {
//            element = "<p>" + this.responseText + "</p>";
//            document.getElementById('central-output').innerHTML = element;
//        }
//    };

    var valueDate = document.getElementById("from-date").value;
    var param = "?from-date=" + valueDate + "&submit=GETDATA";

    xhttp.open("POST", "/cj" + param, true);
    xhttp.send();

}

// Remove any element from data area
function clearCentralElement() {
    document.getElementById('central-output').firstChild.remove();
}

// Read released receipts
function readReleasedReceipts() {

    var xhttp = new XMLHttpRequest();

    var head = buildHeader();
    head = head + "<th class=\"w3-border\">Статус</th></tr>";

    var element = "<table id=\"central-table\" class=\"w3-center\">" + head + "</table>";

    var valueDate = document.getElementById("from-date").value;
    var param = "?from-date=" + valueDate + "&submit=HISTORY";

    xhttp.open("GET", "/cj" + param, true);
    xhttp.send();

    document.getElementById('central-output').innerHTML = element;

}

function getValueFromLink() {

    var link = document.getElementById('rowid').innerHTML;

    var xhttp = new XMLHttpRequest();

    param = "?key=" + link;

    xhttp.open("GET", "/single" + param, true);
    xhttp.send();

    alert(link);

}
// Create an income receipt
function createIncomeReceipt() {
    window.open("income.html");
}

// Create an outgoing receipt
function createOutgoingReceipt() {
    window.open("outgoing.html");
}