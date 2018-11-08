package org.bibsonomy.rest.strategy.persons;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonMatch;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.strategy.AbstractUpdateStrategy;
import org.bibsonomy.rest.strategy.Context;

import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import static org.bibsonomy.util.ValidationUtils.present;

public class PostPersonMergeStrategy extends AbstractUpdateStrategy {
    private final static int PUBLICATION_COUNT = 100;
    private final String personIdTarget, personIdSource;

    public PostPersonMergeStrategy(Context context, String personIdSource, String personIdTarget) {
        super(context);
        if (!present(personIdSource)) throw new IllegalArgumentException("No personId given for the source.");
        if (!present(personIdTarget)) throw new IllegalArgumentException("No personId given for the target.");
        this.personIdSource = personIdSource;
        this.personIdTarget = personIdTarget;
    }

    private List<Post> getAllPostsForPerson(Person person) {
        LinkedList<Post> result = new LinkedList<>();
        List<Post<BibTex>> posts;
        for (int start = 0; ; start += PUBLICATION_COUNT) {
            posts = this.getLogic().getPosts(BibTex.class, GroupingEntity.USER, person.getUser(), null, null,
                    null, null, null, null, null, null,
                    start, start + PUBLICATION_COUNT);
            result.addAll(posts);
            if (posts.size() < PUBLICATION_COUNT) break;
        }
        return result;
    }

    @Override
    protected void render(Writer writer, String resourceID) {
        this.getRenderer().serializePersonId(writer, resourceID);
    }

    @Override
    protected String update() {
        final Person person1 = this.getLogic().getPersonById(PersonIdType.PERSON_ID, personIdSource);
        if (!present(person1)) {
            throw new BadRequestOrResponseException("No person with id " + personIdSource + " as source.");
        }
        person1.setPersonId(personIdSource);
        final Person person2 = this.getLogic().getPersonById(PersonIdType.PERSON_ID, personIdTarget);
        if (!present(person2)) {
            throw new BadRequestOrResponseException("No person with id " + personIdTarget + " as target.");
        }
        person2.setPersonId(personIdTarget);
        PersonMatch personMatch = new PersonMatch();
        personMatch.setPerson1(person1);
        personMatch.setPerson2(person2);
        personMatch.setPerson1Posts(getAllPostsForPerson(person1));
        personMatch.setPerson2Posts(getAllPostsForPerson(person2));
        personMatch.setState(0);
        this.getLogic().acceptMerge(personMatch);
        return person1.getPersonId();
    }
}
