//提交评论
function question_post(){
    var questionId = $("#question_hidden").val();
    var comment = $(".question-textarea").val();

    comment_fz(questionId, 1, comment)
    $(".question-textarea").val("");
}

//点赞评论
function question_inclike(e){
    var commentId = e.getAttribute("data-commentid");
    $.ajax({
        type: "GET",
        url: "/comment/like/" + commentId,
        contentType: "application/json",
        success:function (response){
            alert(response.message);
            window.location.reload();
        },
    })
}

//展开评论
function collapse_comment(e){
    var id = e.getAttribute("data-id");
    var comment = $("#comment-" + id);
    //toggleClass可以用于控制开关切换
    comment.toggleClass("in");

    if(comment.hasClass("in")){
        //展示评论
        e.classList.add("active");
        var commentContainer = $("#comment-" + id);
        if (commentContainer.children().length == 1){
            $.getJSON("/comment/" + id, function (data){
                $.each(data.data.reverse(), function (index, comment){
                    console.log(comment.user);
                    //medialeft
                    var avatarElement = $("<img/>", {
                        "class": "media-object img-rounded question-img",
                        "src": comment.user.avatarUrl
                    });

                    var mediaLeftElement = $("<div/>", {
                        "class": "media-left"
                    });
                    mediaLeftElement.append(avatarElement);

                    //mediabody
                    var mediaBodyElement = $("<div/>", {
                        "class": "media-body"
                    });
                    var mediaBodySpanElement = $("<span/>",{
                        "class": "question-span2",
                        "text": comment.user.name
                    });
                    var mediaBodyDivElement = $("<div/>", {
                        "text": comment.content
                    });
                    var mediaBodySpanTimeElement = $("<span/>", {
                        "class": "pull-right",
                        "text": moment(comment.createtime).format('YYYY-MM-DD hh:mm')
                    });

                    var hrElement = $("<hr/>", {
                        "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 question-hr1"
                    });

                    mediaBodyElement.append(mediaBodySpanElement);
                    mediaBodyElement.append(mediaBodyDivElement);
                    mediaBodyElement.append(mediaBodyDivElement);
                    mediaBodyElement.append(mediaBodySpanTimeElement)

                    //media
                    var mediaElement = $("<div/>", {
                        "class": "media"
                    });
                    mediaElement.append(mediaLeftElement);
                    mediaElement.append(mediaBodyElement);
                    mediaElement.append(hrElement);

                    var commentElement = $("<div/>",{
                        "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12",
                        // html: comment.content
                    });
                    commentElement.append(mediaElement);
                    commentContainer.prepend(commentElement);
                });
            });}
    }else{
        //折叠评论
        e.classList.remove("active")
    }
}


//封装评论提问和评论评论
function comment_fz(parentId, type, content){
    data = {
        "parentId": parentId,
        "type": type,
        "content": content,
    }
    if(!parentId){
        alert("未知的评论ID");
        return;
    }
    if(!content){
        alert("不能回复空内容");
        return;
    }

    $.ajax({
        type: "POST",
        url: "/comment",
        data:JSON.stringify(data),
        contentType: "application/json",
        success: function (response){
            if(response.code == 200){
                alert("回复成功");
                window.location.reload();
            }else if(response.code == 4001){
                var isAccept = confirm("当前用户未登陆，请确认是否跳转登陆");
                if(isAccept){
                    window.open("https://github.com/login/oauth/authorize?client_id=cbdb758ac2a1e2702a19&redirect_uri=http://localhost:8888/callback&scope=user&state=1");
                    window.localStorage.setItem("closeable", true);
                }
            } else{
                alert(response.message);
            }
        },
        dataType: "json"
    });

}

//评论评论
function comment_comment(e){
    var commentId = e.getAttribute("data-id");
    var content = $("#input-" + commentId).val();

    comment_fz(commentId, 2, content)
    $(".comment-content").val("");
}

//选择标签
function selectTag(e) {
    var tag = e.getAttribute("data-tag");
    var previous = $("#tag").val();
    if (previous.indexOf(tag) == -1) {
        if (previous) {
            $("#tag").val(previous + "，" + tag);
        } else {
            $("#tag").val(tag);
        }
    }
}

//展示标题tab
function showSelectTag(){
    $(".publish-tab-div").show();

}