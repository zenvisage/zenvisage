
$('#uploaderForm').on('submit', function(e) {
    document.getElementById("loadingEclipse_upload").style.display = "block";
    document.getElementById("submitButton").style.display = "none";
    e.preventDefault();
    var formData = new FormData(this);
    if (formData.get("csv").name == "" || formData.get("schema").name == "" ) {
      alert("Please select corresponding files!");
      document.getElementById("loadingEclipse_upload").style.display = "none";
      document.getElementById("submitButton").style.display = "block";
      return;
    }
    $.ajax({
        url : $(this).attr('action'),
        type: $(this).attr('method'),
        data: formData,
        processData: false,
        contentType: false,
        success: function (data) {
            $('#dataset-form-control').append($("<option></option>")
                          .attr("value", formData.get("datasetName"))
                          .text( formData.get("datasetName")));
            $('#uploaderModal').modal('toggle');
            alert("Uploaded");
            document.getElementById("loadingEclipse_upload").style.display = "none";
            document.getElementById("submitButton").style.display = "block";
        },
        error: function (jXHR, textStatus, errorThrown) {
            alert(errorThrown);
            document.getElementById("loadingEclipse_upload").style.display = "none";
            document.getElementById("submitButton").style.display = "block";
        }
    });
    log.info("dataset upload: ",$("#datasetNameInput").val())
});

$("#pattern-upload").on('submit', function(e) {
    $('#pattern-upload').modal('toggle');
});

$("#class-creation").on('submit', function(e) {
    $('#class-creation').modal('toggle');
});
