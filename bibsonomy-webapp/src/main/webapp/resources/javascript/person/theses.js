/**
 * on load
 */
$(function () {
    // setup autocomplete for theses
    setupBibtexAuthorSearchForForm("#addThesisAuto", "#addThesisForm");
    //setupBibtexSearchForForm("#addThesisAuto", "#addThesisForm");

    // setup autocomplete for advised theses
    setupBibtexAuthorSearchForForm("#addSupervisedThesisAuto", "#addSupervisedThesisForm");
    //setupBibtexSearchForForm("#addSupervisedThesisAuto", "#addSupervisedThesisForm");
});