package com.eigengo.lift

import java.util.Date

/**
 * The suggestion source ADT
 */
sealed trait SuggestionSource
object SuggestionSource {
  /** Suggestion based on user's exercise history */
  case object History extends SuggestionSource
  /** Suggestion based on user's exercise programme */
  case object Programme extends SuggestionSource
  /** Personal trainer's suggestion, along with notes */
  case class Trainer(notes: String) extends SuggestionSource
}

/**
 * A single suggestion needs a date and the source
 */
sealed trait Suggestion {
  /**
   * The date that the suggestion should be acted upon; e.g. do legs on 29th of February.
   * @return the date with the time element set to midnight
   */
  def date: Date

  /**
   * The source of the suggestion: is it based on the history, trainer's feedback or
   * some exercise programme?
   * @return the source
   */
  def source: SuggestionSource
}

/** Holds implementations of ``Suggestion`` */
object Suggestion {

  type MuscleGroupKey = String

  type ExerciseIntensity = Double

  /**
   * Suggests exercising
   * @param date the date
   * @param source the source
   * @param muscleGroupKeys the target muscle groups
   * @param intensity the intensity
   */
  case class Session(date: Date, source: SuggestionSource, muscleGroupKeys: Seq[MuscleGroupKey], intensity: ExerciseIntensity) extends Suggestion

  /**
   * Suggests resting
   * @param date the date
   * @param source the source
   */
  case class Rest(date: Date, source: SuggestionSource) extends Suggestion
}

/**
 * Wraps the list of Suggestions
 * @param suggestions the suggestions
 */
case class Suggestions(suggestions: List[Suggestion])
