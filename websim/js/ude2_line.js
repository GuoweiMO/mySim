/**
 * Created by kwai on 12/08/14.
 */

    // Load the Visualization API and the Linechart package.
google.load('visualization', '1', {'packages':['corechart']});

// Set a callback to run when the Google Visualization API is loaded.
google.setOnLoadCallback(drawChart2);

function drawChart2() {
    var jsonData = $.ajax({
        url: "src/getUde2.php",
        dataType:"json",
        async: false
    }).responseText;

    var options = {
        title: 'T<0.5 (12 iterations)',
        curveType: 'function',
        legend:{position: 'bottom'},
        explorer: {axis:'true'}
    };
    // Create our data table out of JSON data loaded from server.
    var data = new google.visualization.DataTable(jsonData);

    // Instantiate and draw our chart, passing in some options.
    var chart = new google.visualization.LineChart(document.getElementById('display2'));
    chart.draw(data, options);
}