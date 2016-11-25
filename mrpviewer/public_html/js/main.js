
var now = new Date();

function getCurrentYear() {

    return now.getFullYear();

}

function getCurrentMonth() {

    return now.getMonth();

}
// on focus on the date field
function inputDateDoubleClick() {

    var month = now.getMonth() + 1;
    var current = now.getFullYear() + "-" + month;

    document.getElementById("fromMonth").value = current;

}

function getPurchaseGroup() {

}

function getMaterialNumber() {

}

