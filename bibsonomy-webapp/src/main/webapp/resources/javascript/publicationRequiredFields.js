$(function () {
    myownTagInit($('#myownChkBox'), $('#inpf_tags'));

    function authorWarning() {
        if(isAuthor() && !$("#myownChkBox").is(':checked')){
            $('#iAmAuthorWarning').attr("style", "display:block");
        } else {
            $('#iAmAuthorWarning').attr("style", "display:none");
        }
    }
    authorWarning();

    $("#post\\.resource\\.editor").on('change',authorWarning);
    $("#post\\.resource\\.author").on('change',authorWarning);
    $('#myownChkBox').on('change', authorWarning);
});

function highlightMatches(text, input) {
    var terms = input.split(" ");
    for (var i = 0; i < terms.length; i++) {
        text = highlightMatch(text, terms[i]);
    }
    return text;
}

function highlightMatch(text, term) {
    return text.replace(new RegExp("(?![^&;]+;)(?!<[^<>]*)(" +
            $.ui.autocomplete.escapeRegex(term) +
            ")(?![^<>]*>)(?![^&;]+;)", "gi"
        ), "<strong>$1</strong>"
    );
}

function myownTagInit(chkbox, tagbox) {
    var expr = /((^|[ ])myown($|[ ]))/gi;
    if (!(chkbox.length > 0
        && tagbox.length > 0))
        return;

    if (tagbox.val().search(expr) != -1) {
        chkbox[0].checked = true;
    }

    tagbox.keyup(function () {
            if (tagbox.val().search(expr) != -1) {
                chkbox[0].checked = true;
                return;
            }
            chkbox[0].checked = false;
        }
    );

    chkbox.click(
        function () {
            clearTagInput();
            if (this.checked
                && tagbox.val().search(expr) == -1) {
                tagbox.removeClass('descriptiveLabel').val('myown ' + tagbox.val());
            } else if (!this.checked) {
                tagbox.val(tagbox.val().replace(expr, ' ').replace(/^[ ]?/, ''));
            }
        })
        .parent().removeClass('hiddenElement');
}

function isAuthor() {
    var allPossibleNames = [];
    if(!$("#claimedPersonMainNameID").length){ //uses the User.realName as a fallback if no Person was claimed by the user
        var individualUserRealName = $('#userRealnameID').val();
        allPossibleNames.push(individualUserRealName);
    } else { //uses all names saved in the person claimed by the current user
        var userClaimedPersonMainName = $('#claimedPersonMainNameID').val();
        allPossibleNames.push(userClaimedPersonMainName);
        var userClaimedPersonNames = $('#claimedPersonNamesID').val();
        allPossibleNames = allPossibleNames.concat(userClaimedPersonNames.split(" and ")).filter(function(n){ return n });
    }

    var enteredAuthors = $("#post\\.resource\\.author").val();
    var individualEnteredAuthors = enteredAuthors.split("\n");
    var enteredEditors = $("#post\\.resource\\.editor").val();
    individualEnteredAuthors = individualEnteredAuthors.concat(enteredEditors.split("\n")).filter(function(n){ return n });

    if (enteredAuthors.length !== 0 || enteredEditors.length !== 0){
        var output = individualEnteredAuthors.filter(function (obj) {
            return allPossibleNames.indexOf(obj) !== -1;
        });
        return output.length!==0
    } else{ //Fallback if the input was left empty or the input was deleted
        return false;
    }
}