$ ->
  $.get "/persons", (persons) ->
    $.each persons, (index, person) ->
      name = $("<span style=' display: inline'>").addClass("name").text person.name
      phoneNumb = $("<span style='margin-left: 3%; display: inline'>").addClass("phoneNumb").text person.phoneNumb

      $("#persons").append $("<li style=''>").append(name).append(phoneNumb)