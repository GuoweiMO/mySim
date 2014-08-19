/**
 * Created by kwai on 11/08/14.
 */
/**
 * Created by kwai on 10/08/14.
 */
sigma.classes.graph.addMethod('neighbors', function (nodeId) {
    var k;
    var ngbNodes = {},
        index = this.allNeighborsIndex[nodeId] || {};

    for (k in index)
        ngbNodes[k] = this.nodesIndex[k];

    return ngbNodes;
});

sigma.parsers.json('data/map_data.json', {
        container: 'b_net',
        settings: {
            defaultNodeColor: '#00FF00'
        }
    },

    function (sig) {
        // We first need to save the original colors of our nodes and edges, like this:
        sig.graph.nodes().forEach(function (n) {
            n.originalColor = n.color;
        });

        sig.graph.edges().forEach(function (e) {
            e.originalColor = e.color;
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
        sig.bind('clickStage', function (eve) {
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
