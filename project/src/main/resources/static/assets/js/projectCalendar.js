let calendarEl = document.getElementById('calendar');

var calendar = new FullCalendar.Calendar( 
calendarEl , 
{
    selectable          : true  ,
    timeZone            : 'UTC',
    themeSystem         : 'bootstrap5',
    droppable           : true,
    locale              : 'ko',
    timeFormat          : 'HH:mm',
    headerToolbar       :      
    {
        left            : 'prev,next today',
        center          : 'title',
        right           : 'dayGridMonth,timeGridWeek,timeGridDay,listWeek'
    },
    allDaySlot          : false,
    eventLimit          : true,
    dayMaxEvents        : true,
    views               : 
    { 
        dayGridMonth    : { eventLimit : 12 }
    },
    minTime             : '00:00:00',
    maxTime             : '24:00:00',   
    // events              : dateEvents,
    weekends            : true,
    nowIndicator        : true,
    eventDisplay        : "block",
    eventLimitClick     : 'week',
    eventSources        :
    [{
        events          :
        (info,successCallback) => {
            $.ajax({
                url     : `/api/teams/${teamId}/calendar?start=${moment(info.startStr).format("YYYY-MM-DDT00:00")}&end=${moment(info.endStr).format("YYYY-MM-DDT00:00")}`,
                type    : "GET",
                success : 
                (data)=>{
                    successCallback(data.data);
                }
            });
        }
    }],

    // **********EVENT********** //
    eventClick          : 
    (e)=>{
        const data = {
            ...e.event.extendedProps,
            groupId                     : e.event.groupId,
            start                       : e.event.startStr,
            end                         : e.event.endStr,
            title                       : e.event.title,
            color                       : e.event.backgroundColor, 
            allDay                      : e.event.allDay                      
        }; 
        calendarDetailViewEvent("#calendar_detail_modal", data);
    },
    eventMouseEnter         : 
    (info)=>{
        const data = {
            ...info.event.extendedProps,
            groupId                     : info.event.groupId,
            start                       : info.event.startStr,
            end                         : info.event.endStr,
            title                       : info.event.title,
            color                       : info.event.backgroundColor,
            allDay                      : info.event.allDay
        }; 
    
        $(info.el).popover({
            title                       :  $('<div/>', {
                class                   : "popover-date-header",
                text                    : data.title
            }).css({    
                'backgroundColor'       : data.color,
                'color'                 : getTextColorByBackgroundColor(data.color)
            }),
            placement                   : 'top',
            trigger                     : 'hover',
            content                     : $('<div>', {
                class                   : 'popoverInfoCalendar'
            }).append('<p><strong>?????????:</strong> ' + data.memberNickname + '</p>')
            .append('<p><strong>??????:</strong> ' + getDisplayEventDate(data) + '</p>')
            .append('<hr/>')
            .append('<div class="popoverDescCalendar"><strong>??????:</strong> ' + data.memo + '</div>'),
            delay                       : 
            {
                show                    : 500,
                hide                    : 50
            }, 
            html                        : true,
            container                   : 'body'
        });
    },

    eventDidMount : (info) => {    
        if(!filtering(info.event)){
            info.el.style.display = "none";
        }  
    }
});

calendar.render();

function filtering(event) {
    var show_type = true;

    var types = $('#type_filter').val();
    if (types && types.length > 0) {
      if (types[0] == 0) {
        show_type = true;
      } else {
        show_type = types.indexOf(event.groupId) >= 0;
      }
    }
    return show_type;
}

const calendarDetailViewEvent = (id, data) => {
    const element = $(id + " .modal-header");

    element.css("backgroundColor", data.color);
    element.css("color", getTextColorByBackgroundColor(data.color));

    const div = $("<div/>",{
        class : "col w-100"
    })
    .append("<div class='mb-3'>")
    .append("<div>")
    .append("<span class='text-muted fs-6'>?????? : </span>")
    .append(`<span class='text-primary'>${data.title}`).append("</span>")
    .append("</div>")
    .append("<div>")
    .append("<span class='text-muted fs-6'>????????? : </span>")
    .append(`<span class='text-primary'>${data.memberNickname}`).append("</span>")
    .append("</div>")
    .append("<div style='font-size : 20px'>")
    .append("<div>")
    .append("<span class='text-muted fs-6'>?????? : </span>")
    .append(`<span>${getModalEventDate(data)}`).append("</span>")
    .append("</div>")
    .append("<div><hr/>")
    .append("<span class='text-muted fs-6'>??????</span>")
    .append(`<div class='mb-2'><p style='white-space : pre-line'>${data.memo}`).append("</p></div>")
    .append("</div>")
    .append("</div>");

    if(loginMemberId == data.calendarId || teamLeaderId == loginMemberId){
        const footerDiv = $("<div/>",{
            class : "calendar_update_btn"
        });
        footerDiv.append(`<a href="/teams/${teamId}/calendar/${data.calendarId}/edit" class="btn btn-warning">??????`).append("</a>")
        $(id + " .modal-footer").prepend(footerDiv);
    }

    $(id + " .modal-body").html(div);

    let myModal = new bootstrap.Modal(document.querySelector(id), {
        keyboard: false
    });
    myModal.show();
    $(id).on("hidden.bs.modal", ()=>{
        const cub = $(".calendar_update_btn");
        if(cub){
            cub.remove();
        }      
    });
}



const getTextColorByBackgroundColor = (hexColor)=> {
    // ?????? ?????? # ??????
    const c = hexColor.substring(1);
    // rrggbb??? 10????????? ??????
    const rgb = parseInt(c, 16);
    // red ??????
    const r = (rgb >> 16) & 0xff;
    // green ?????? 
    const g = (rgb >>  8) & 0xff; 
    // blue ??????
    const b = (rgb >>  0) & 0xff;  
    // per ITU-R BT.709
    const luma = 0.2126 * r + 0.7152 * g + 0.0722 * b;
    // ?????? ??????
    return luma < 127.5 ? "white" : "black";
}

const getDisplayEventDate = (event) => {
    let displayEventDate;
    if (event.allDay == false) {
        let startTimeEventInfo = moment(event.start).format('HH:mm');
        let endTimeEventInfo = moment(event.end).format('HH:mm');
        displayEventDate = startTimeEventInfo + " - " + endTimeEventInfo;
    } else {
        displayEventDate = "????????????";
    }


    return displayEventDate;
}

const getModalEventDate = (event) => {
    let displayEventDate;

    let startEventInfo = moment(event.start).format('YYYY-MM-DD');
    let endEventInfo = moment(event.end).format('YYYY-MM-DD');

    if(event.dateType == "MONTH"){
        if(endEventInfo == "Invalid date"){
            displayEventDate = "????????????"
        }else{
            displayEventDate = startEventInfo + " - " + endEventInfo;  
        }  
    }else{
        if(event.allDay){
            displayEventDate = "????????????"
        }else{         
            let startTimeEventInfo;
            let endTimeEventInfo;
            if(startEventInfo == endEventInfo){
                startTimeEventInfo = moment(event.start).format('HH:mm');
                endTimeEventInfo = moment(event.end).format('HH:mm');
            }else{
                startTimeEventInfo = moment(event.start).format('YYYY-MM-DD HH:mm');
                endTimeEventInfo = moment(event.end).format('YYYY-MM-DD HH:mm');
            }
          
            displayEventDate = startTimeEventInfo + " - " + endTimeEventInfo;  
        } 
    }
   
    return displayEventDate;
}

$('.filter').on('change', function (e) {
    calendar.destroy();
    calendar.render();
});

$("#type_filter").select2({
    placeholder: "??????..",
    allowClear: true
});
