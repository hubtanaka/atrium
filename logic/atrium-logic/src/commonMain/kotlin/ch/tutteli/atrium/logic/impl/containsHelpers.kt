package ch.tutteli.atrium.logic.impl

import ch.tutteli.atrium.assertions.Assertion
import ch.tutteli.atrium.assertions.AssertionGroup
import ch.tutteli.atrium.assertions.builders.assertionBuilder
import ch.tutteli.atrium.assertions.builders.invisibleGroup
import ch.tutteli.atrium.assertions.builders.withExplanatoryAssertion
import ch.tutteli.atrium.core.None
import ch.tutteli.atrium.core.Some
import ch.tutteli.atrium.core.falseProvider
import ch.tutteli.atrium.core.trueProvider
import ch.tutteli.atrium.creating.AssertionContainer
import ch.tutteli.atrium.creating.Expect
import ch.tutteli.atrium.logic.collectBasedOnSubject
import ch.tutteli.atrium.logic.creating.collectors.collectAssertions
import ch.tutteli.atrium.logic.hasNext
import ch.tutteli.atrium.reporting.Text
import ch.tutteli.atrium.reporting.translating.Translatable
import ch.tutteli.atrium.reporting.translating.TranslatableWithArgs
import ch.tutteli.atrium.translations.DescriptionAnyExpectation.TO_EQUAL
import ch.tutteli.atrium.translations.DescriptionIterableLikeExpectation
import ch.tutteli.kbox.identity

internal fun <E : Any> allCreatedAssertionsHold(
    container: AssertionContainer<*>,
    subject: E?,
    assertionCreator: (Expect<E>.() -> Unit)?
): Boolean = when (subject) {
    null -> assertionCreator == null
    else -> assertionCreator != null && container.collectBasedOnSubject(Some(subject), assertionCreator).holds()
}

internal fun <E : Any> createExplanatoryAssertionGroup(
    container: AssertionContainer<*>,
    assertionCreatorOrNull: (Expect<E>.() -> Unit)?
): AssertionGroup = assertionBuilder
    .explanatoryGroup
    .withDefaultType
    .let {
        // we don't use toBeNullOrNullGivenElse because we want to report an empty assertionCreatorOrNull and
        // since we use None as subject and are already inside an explanatory assertion group, it would not be reported
        if (assertionCreatorOrNull != null) {
            // we don't use a subject, we will not show it anyway
            it.collectAssertions(container, None, assertionCreatorOrNull)
        } else {
            it.withAssertion(
                // it is for an explanatoryGroup where it does not matter if the assertion holds or not
                // thus it is OK to use trueProvider
                assertionBuilder.createDescriptive(TO_EQUAL, Text.NULL, trueProvider)
            )
        }
    }
    .build()

internal fun <E> createIndexAssertions(
    list: List<E>,
    predicate: (IndexedValue<E>) -> Boolean
) = list
    .asSequence()
    .withIndex()
    .filter { predicate(it) }
    .map { (index, element) ->
        assertionBuilder.createDescriptive(
            TranslatableWithArgs(DescriptionIterableLikeExpectation.INDEX, index),
            element,
            falseProvider
        )
    }
    .toList()

internal fun createExplanatoryGroupForMismatches(
    mismatches: List<Assertion>
): AssertionGroup {
    return assertionBuilder.explanatoryGroup
        .withWarningType
        .withAssertion(
            assertionBuilder.list
                .withDescriptionAndEmptyRepresentation(DescriptionIterableLikeExpectation.WARNING_MISMATCHES)
                .withAssertions(mismatches)
                .build()
        )
        .failing
        .build()
}

internal fun createAssertionGroupFromListOfAssertions(
    description: Translatable,
    representation: Any?,
    assertions: List<Assertion>
): AssertionGroup =
    if (assertions.isEmpty())
        assertionBuilder.invisibleGroup
            .withAssertion(
                assertionBuilder.createDescriptive(description, representation, trueProvider)
            ).build()
    else assertionBuilder.list
        .withDescriptionAndRepresentation(description, representation)
        .withAssertions(assertions)
        .build()

internal fun <E> decorateAssertionWithHasNext(
    assertion: AssertionGroup,
    listAssertionContainer: AssertionContainer<List<E>>
): AssertionGroup {
    val hasNext = listAssertionContainer.hasNext(::identity)
    return if (hasNext.holds()) {
        assertion
    } else {
        assertionBuilder.invisibleGroup
            .withAssertions(
                hasNext,
                assertionBuilder.explanatoryGroup
                    .withDefaultType
                    .withAssertion(assertion)
                    .build()
            )
            .build()
    }
}

internal fun <E> decorateWithHintUseNotToHaveElementsOrNone(
    assertion: AssertionGroup,
    listAssertionContainer: AssertionContainer<List<E>>,
    notToHaveNextOrNoneFunName: String
): AssertionGroup {
    val hasNext = listAssertionContainer.hasNext(::identity)
    return if (!hasNext.holds()) {
        assertionBuilder.invisibleGroup
            .withAssertions(
                assertion,
                assertionBuilder.explanatoryGroup
                    .withHintType
                    .withExplanatoryAssertion(
                        TranslatableWithArgs(
                            DescriptionIterableLikeExpectation.USE_NOT_TO_HAVE_ELEMENTS_OR_NONE,
                            notToHaveNextOrNoneFunName
                        )
                    )
                    .build()
            )
            .build()
    } else {
        assertion
    }
}
