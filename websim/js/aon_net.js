/**
* Created by kwai on 10/08/14.
*/

function graphInit()
{
    sigma.parsers.json('data/map_data.json', {
            container: 'aon_net',
            settings: {
                defaultNodeColor: '#66FF66' //light green
            }
        },

        function (sig) {

            var myObject = {};
            $.get("data/AoNA_Flows(for html)", function (file) {
                var lines = file.split("\n");

                for (var i  in lines) {
                    myObject[i] = lines[i];
                    sig.graph.edges().forEach(function (e) {

                        if (e.id == myObject[i].split(" ")[0]) {
                            var flow = myObject[i].split(" ")[1];
                            if (Number(flow) == 0){
                                e.color = "#B2B2B2";
                            }
                            if (Number(flow) > 0 && Number(flow) < 200) {
                                e.color = "#66FF66";
                            }
                            if (Number(flow) > 200 && Number(flow) < 400) {
                                e.color = "#009900";
                            }
                            if (Number(flow) > 400 && Number(flow) < 600) {
                                e.color = "#FFFF00";
                            }
                            if (Number(flow) > 600 && Number(flow) < 800) {
                                e.color = "#FF9900";
                            }
                            if (Number(flow) > 800 && Number(flow) < 1000) {
                                e.color = "#FF0000";
                            }
                            if (Number(flow) > 1000) {
                                e.color = "#660033";
                            }
                        }

                        e.originalColor = e.color;
                    });
                }

                sig.refresh();

            }, 'text');


            // We first need to save the original colors of our nodes and edges, like this:
            sig.graph.nodes().forEach(function (n) {
                n.originalColor = n.color;
            });


            // When a node is clicked, we check for each node if it is a neighbor of the clicked one.
            // If not,we set its color as grey, and else, it takes its original color.
            // We do the same for the edges, and we only keep edges that have both extremities colored.
            sig.bind('clickNode', function (e) {
                var nodeId = e.data.node.id;
                var toKeep = sig.graph.neighbors(nodeId);
                toKeep[nodeId] = e.data.node;

                sig.graph.nodes().forEach(function (n) {
                    if (toKeep[n.id])
                        n.color = n.originalColor;
                    else
                        n.color = '#eee';
                });

                sig.graph.edges().forEach(function (e) {
                    if (toKeep[e.source] && toKeep[e.target])
                        e.color = e.originalColor;
                    else
                        e.color = '#eee';
                });

                // Since the data has been modified, we need to call the refresh method to make the colors
                // update effective.
                sig.refresh();
            });

            // When the stage is clicked, we just color each node and edge with its original color.
            sig.bind('clickStage', function (e) {
                sig.graph.nodes().forEach(function (n) {
                    n.color = n.originalColor;
                });

                sig.graph.edges().forEach(function (e) {
                    e.color = e.originalColor;
                });

                // Same as in the previous event:
                sig.refresh();
            });
        }
    );
}