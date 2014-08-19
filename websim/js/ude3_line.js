/**
 * Created by kwai on 12/08/14.
 */

    // Load the Visualization API and the Linechart package.
google.load('visualization', '1', {'packages':['corechart']});

// Set a callback to run when the Google Visualization API is loaded.
google.setOnLoadCallback(drawChart3);

function drawChart3() {
    var jsonData = $.ajax({
        url: "src/getUde3.php",
        dataType:"json",
        async: false
    }).responseText;

    var options = {
        title: 'T<0.1 (24 iterations)',
        curveType: 'function',
        legend:{position: 'bottom'},
        explorer: {axis:'true'}
    };
    // Create our data table out of JSON data loaded from server.
    var data = new google.visualization.DataTable(jsonData);

    // Instantiate and draw our chart, passing in some options.
    var chart = new google.visualization.LineChart(document.getElementById('display3'));
    chart.draw(data, options);
}