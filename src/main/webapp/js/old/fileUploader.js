
$('#uploaderForm').on('submit', function(e) {
    e.preventDefault();
    var formData = new FormData(this);
    if (formData.get("csv").name == "" || formData.get("schema").name == "" ) {
      alert("Please select corresponding files!");
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
        },
        error: function (jXHR, textStatus, errorThrown) {
            alert(errorThrown);
        }
    });
});