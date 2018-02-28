app.controller('fileuploadController', [
      '$scope', '$rootScope', '$cookies', 'datasetInfo',
      function($scope, $rootScope, $cookies, datasetInfo){

  var formData;
  var datasetNameInput;
  $('#uploaderForm').on('submit', function(e) {
    e.preventDefault();
    
    if($cookies.getObject('userinfo') || !login_ava){
      formData = new FormData(this);
      if (formData.get("csv").name.split(".").pop() != "csv" ){
        alert("Please select a csv file");
        return;
      }
      else if (formData.get("csv").size > 100000000) {
        alert("Do not upload files over 100MB");
        return;
      }
      parseCSV(formData);
      datasetNameInput = $("#datasetNameInput").val();
      console.log('test:',$(this).attr('action'),$(this).attr('method'));
      log.info("dataset upload: ",$("#datasetNameInput").val())
      // $('#uploaderModal').modal('toggle');
      document.getElementById("uploadingProgressMessage").style.display = "block";
      document.getElementById("submitButton").style.display = "none";
    }else{
      alert("Please log in first to upload dataset.")
    }

  });

  // function getCheckedAttributes(){
  //   $("input:checkbox[name=type]:checked").each(function(){
  //       console.log($(this).val());
  // }

  $("#define-attributes").on('submit', function(e) {
       var attributeList =  [];
       var selectedAxis =  [];
    // var xList = [];
    // var yList = [];
    // var zList = [];

    $(".types").each(function(){
        var selectedXOption = $(this).children("option").filter(":selected").text()
        attributeList.push($(this).val() + " " + selectedXOption);
        selectedAxis.push(["true", "true", "true"])
        // var selectedYOption = $(this).children("option").filter(":selected").text()
        // yList.push($(this).val() + " " + selectedYOption);
        // var selectedZOption = $(this).children("option").filter(":selected").text()
        // zList.push($(this).val() + " " + selectedZOption);
    });

    var selectedAttributes = filterUncheckAttributes(attributeList,selectedAxis)
    var selectedAttributesParsed = []
    for (i = 0; i < selectedAttributes.length; i++) {
        temp = selectedAttributes[i].split(" ");
                console.log("final selected: ", temp);
         selectedAttributesParsed.push({name:temp[0],type:temp[1],selectedX:temp[2],selectedY:temp[3],selectedZ:temp[4]})

    }
    $('#define-attributes').modal('toggle');

    var xyzQuery = {datasetName:datasetNameInput, variables:selectedAttributesParsed};
    console.log(xyzQuery);
    var myObject = JSON.stringify(xyzQuery);

    var username;
    if($cookies.getObject('userinfo')){
      username = $cookies.getObject('userinfo')['username'][0];
    }else{
      username = "public";
    }
    var insertUserTablePair = {'userName':username, 'datasetName':formData.get("datasetName")};

    $.ajax({
        type: 'POST',
        url: '/zv/selectXYZ',
        data: myObject,
        contentType: 'application/json; charset=utf-8',
      success: function (data) { console.log('called1');
        $.ajax({
            url : '/zv/fileUpload',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function (data) {console.log('called2');
                $.ajax({
                  url : '/zv/insertUserTablePair',
                  type: 'POST',
                  data: insertUserTablePair,
                  success: function(response){

                    // if(username == 'public'){
                    //   alert("Inserted as public dataset");
                    // }else{
                      var today = new Date();
                      var expiresValue = new Date(today);
                      //Set 'expires' option in 2 hours
                      expiresValue.setMinutes(today.getMinutes() + 120);
                      $cookies.putObject("userinfo",response,{'expires': expiresValue})
                      // angular.element($('#sidebar')).scope().updatetablelist(response['tablelist']);
                      datasetInfo.storetablelist(response['tablelist']);
                      $scope.tablelist = datasetInfo.getTablelist();
                      // console.log("table inserted into your account successfully")
                    // }

                  },
                  error: function(response){
                    alert("fail to insert dataset into your account")
                  }
                });

                $('#dataset-form-control').append($("<option></option>")
                              .attr("value", formData.get("datasetName"))
                              .text( formData.get("datasetName")));
                              document.getElementById("uploadingProgressMessage").style.display = "block";
                              document.getElementById("submitButton").style.display = "none";
                alert("Upload successful");
                location.reload();
            },
            error: function (jXHR, textStatus, errorThrown) {
              $("#errorModalText").html("Server error. Have you tried the dataset upload instructions in the following link? <a href='https://github.com/zenvisage/zenvisage/wiki/Instructions-for-uploading-new-datasets'>here?</a>");
              $("#errorModal").modal()
                document.getElementById("uploadingProgressMessage").style.display = "block";
                document.getElementById("submitButton").style.display = "none";
            }
        });

        },
        error: function (jXHR, textStatus, errorThrown) {
          $("#errorModalText").html("Server error. Have you tried the dataset upload instructions in the following link? <a href='https://github.com/zenvisage/zenvisage/wiki/Instructions-for-uploading-new-datasets'>here?</a>");
          $("#errorModal").modal()
          document.getElementById("uploadingProgressMessage").style.display = "block";
          document.getElementById("submitButton").style.display = "none";

        }
    });


  });


}]);
