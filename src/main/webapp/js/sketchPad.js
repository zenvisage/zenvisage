function uploadToSketchpadNew( draggableId, graphType )
{
    var draggedGraph;
    //var xType, yType, zType;
    switch( graphType ) {
        case "representativeQuery":
            draggedGraph = representativeDygraphsNew[draggableId]["data"];
            // xType = representativeDygraphsNew[draggableId]["xType"];
            // yType = representativeDygraphsNew[draggableId]["yType"];
            // zType = representativeDygraphsNew[draggableId]["zType"];
            break;
        case "outlierQuery":
            draggedGraph = outlierDygraphsNew[draggableId]["data"];
            // xType = outlierDygraphsNew[draggableId]["xType"];
            // yType = outlierDygraphsNew[draggableId]["yType"];
            // zType = outlierDygraphsNew[draggableId]["zType"];
            break;
        default: //userQuery
            draggedGraph = userQueryDygraphsNew[draggableId]["data"];
        // xType = userQueryDygraphsNew[draggableId]["xType"];
        // yType = userQueryDygraphsNew[draggableId]["yType"];
        // zType = userQueryDygraphsNew[draggableId]["zType"];

    }
    if($("#graph-type").children("option:selected").val() == "string:Line"){
        plotSketchpadNewHelper( draggedGraph );//, xType, yType, zType);
    }
    else{
        plotSketchpadNewScatterHelper( draggedGraph );
    }

}

$(document).ready(function(){
    // $('#add-row').click(function(){
    //   addRow();
    // });

    $("#draw-div").droppable({
        accept: ".draggable-graph",
        drop: function( event, ui )
        {
            log.info("dropped successfully to canvas")
            uploadToSketchpadNew($(ui.draggable).attr('id'), $(ui.draggable).data('graph-type'));
        }
    });

    $("#scatter-div").droppable({
        accept: ".draggable-graph",
        drop: function( event, ui )
        {
            log.info("dropped successfully to canvas")
            currentZLabel = $(ui.draggable).siblings('text').attr('label')
            uploadToSketchpadNew($(ui.draggable).attr('id'), $(ui.draggable).data('graph-type'));
        }
    });
});