# MessageFormat 2.0 Default Function Registry

This section defines the **REQUIRED** _functions_ which are REQUIRED for conformance with this specification,
along with **RECOMMENDED** _functions_ that SHOULD be implemented to support
additional functionality.

To **_<dfn>accept</dfn>_** a function means that an implementation MUST NOT
emit an _Unknown Function_ error for that _function_'s _identifier_.
To _accept_ an _option_ means that a _function handler_ MUST NOT
emit a _Bad Option_ error for that _option_'s _identifier_ when used with the _function_
it is defined for
and MUST NOT emit a _Bad Option_ error for any of the _option_ values
defined for that _option_.
Accepting a _function_ or its _options_ does not mean that a particular output is produced.
Implementations MAY emit an _Unsupported Operation_ error for _options_
or _option_ values that they cannot support.

_Functions_ can define _options_. 
An _option_ can be **REQUIRED** or **RECOMMENDED**.

Implementations MUST _accept_ each **REQUIRED** _function_ and
MUST _accept_ all _options_ defined as **REQUIRED** for those _functions_.

Implementations SHOULD _accept_ each **RECOMMENDED** _function_. 
For each such _function_, the implementation MUST accept all _options_
listed as **REQUIRED** for that _function_.

Implementations SHOULD _accept_ _options_ that are marked as **RECOMMENDED**.

Implementations MAY _accept_ _functions_ not defined in this specification.
In addition, implementations SHOULD provide mechanisms for users to
register and use user-defined _functions_ and their associated _functional handlers_.
Functions not defined by any version of this specification SHOULD use 
an implementation-defined or user-defined _namespace_.

Implementations MAY implement additional _options_ not defined
by any version of this specification
for **REQUIRED** and **RECOMMENDED** functions.
Such _options_ MUST use an implementation-specific _namespace_.

Implementations MAY _accept_, for _options_ defined in this specification,
_option_ values which are not defined in this specification.
However, such values might become defined with a different meaning in the future,
including with a different, incompatible name
or using an incompatible value space.
Supporting implementation-specific _option_ values for **REQUIRED** or **RECOMMENDED** functions is NOT RECOMMENDED.

Implementations MAY _accept_, for _operands_ or _options_ defined in this specification,
values with implementation-defined types.
Such values can be useful to users in cases where local usage and support exists
(including cases in which details vary from those defined by Unicode and CLDR).

> For example:
> - Implementations are encouraged to _accept_ some native representation
>   for currency amounts as the _operand_ in the _function_ `:currency`.
> - A Java implementation might _accept_ a `java.time.chrono.Chronology` object
>   as a value for the _date/time override option_ `calendar`
> - ICU4J's implementation might _accept_ a `com.ibm.icu.text.NumberingSystem` object
>   instead of using a [Unicode Numbering System Identifier](https://cldr-smoke.unicode.org/spec/main/ldml/tr35.html#UnicodeNumberSystemIdentifier)
>   for the option `numberingSystem` in _functions_ such as `:number` or `:integer`.

Future versions of this specification MAY define additional _options_ and _option_ values,
subject to the rules in the [Stability Policy](#stability-policy),
for _functions_ found in this specification.
As implementations are permitted to ignore _options_ that they do not support,
it is possible to write _messages_ using _options_ not defined below
which currently format with no error, but which could produce errors
when formatted with a later edition of this specification.
Therefore, using _options_ not explicitly defined here is NOT RECOMMENDED.

## String Value Selection and Formatting

### The `:string` function

The function `:string` provides string selection and formatting.

#### Operands

The _operand_ of `:string` is either any implementation-defined type
that is a string or for which conversion to a string is supported,
or any _literal_ value.
All other values produce a _Bad Operand_ error.

> For example, in Java, implementations of the `java.lang.CharSequence` interface
> (such as `java.lang.String` or `java.lang.StringBuilder`),
> the type `char`, or the class `java.lang.Character` might be considered
> as the "implementation-defined types".
> Such an implementation might also support other classes via the method `toString()`.
> This might be used to enable selection of a `enum` value by name, for example.
>
> Other programming languages would define string and character sequence types or
> classes according to their local needs, including, where appropriate,
> coercion to string.

#### Options

The function `:string` has no _options_.

> [!NOTE]
> While `:string` has no built- in _options_,
> _options_ in the `u:` _namespace_ can be used. 
> For example: 
>```
> {$s :string u:dir=ltr u:locale=fr-CA}
>```
#### Resolved Value

The _resolved value_ of an _expression_ with a `:string` _function_
contains the string value of the _operand_ of the annotated _expression_,
together with its resolved locale and directionality.
None of the _options_ set on the _expression_ are part of the _resolved value_.

#### Selection

When implementing [`MatchSelectorKeys(resolvedSelector, keys)`](/spec/formatting.md#resolve-preferences)
where `resolvedSelector` is the _resolved value_ of a _selector_
and `keys` is a list of strings,
the `:string` selector function performs as described below.

1. Let `compare` be the string value of `resolvedSelector`
   in Unicode Normalization Form C (NFC) [\[UAX#15\]](https://www.unicode.org/reports/tr15)
1. Let `result` be a new empty list of strings.
1. For each string `key` in `keys`:
   1. If `key` and `compare` consist of the same sequence of Unicode code points, then
      1. Append `key` as the last element of the list `result`.
1. Return `result`.

> [!NOTE]
> Unquoted string literals in a _variant_ do not include spaces.
> If users wish to match strings that include whitespace
> (including U+3000 `IDEOGRAPHIC SPACE`)
> to a key, the `key` needs to be quoted.
>
> For example:
> ```
> .input {$string :string}
> .match $string
> | space key | {{Matches the string " space key "}}
> *             {{Matches the string "space key"}}
> ```

#### Formatting

The `:string` function returns the string value of the _resolved value_ of the _operand_.

> [!NOTE]
> The function `:string` does not perform Unicode Normalization of its formatted output.
> Users SHOULD encode _messages_ and their parts in Unicode Normalization Form C (NFC)
> unless there is a very good reason not to.

## Numeric Value Selection and Formatting

### The `:number` function

The function `:number` is a selector and formatter for numeric values.

#### Operands

The function `:number` requires a [Number Operand](#number-operands) as its _operand_.

#### Options

Some options do not have default values defined in this specification.
The defaults for these options are implementation-dependent.
In general, the default values for such options depend on the locale, 
the value of other options, or both.

> [!NOTE]
> The names of _options_ and their _values_ were derived from the
> [options](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Intl/NumberFormat/NumberFormat#options)
> in JavaScript's `Intl.NumberFormat`.

The following options and their values are required to be available on the function `:number`:
- `select`
   -  `plural` (default; see [Default Value of `select` Option](#default-value-of-select-option) below)
   -  `ordinal`
   -  `exact`
- `compactDisplay` (this option only has meaning when combined with the option `notation=compact`)
   - `short` (default)
   - `long`
- `notation`
   - `standard` (default)
   - `scientific`
   - `engineering`
   - `compact`
- `numberingSystem`
   - valid [Unicode Number System Identifier](https://cldr-smoke.unicode.org/spec/main/ldml/tr35.html#UnicodeNumberSystemIdentifier)
     (default is locale-specific)
- `signDisplay`
   -  `auto` (default)
   -  `always`
   -  `exceptZero`
   -  `negative`
   -  `never`
- `style`
  - `decimal` (default)
  - `percent` (see [Percent Style](#percent-style) below)
- `useGrouping`
  - `auto` (default)
  - `always`
  - `never`
  - `min2`
- `minimumIntegerDigits`
  - ([digit size option](#digit-size-options), default: `1`)
- `minimumFractionDigits`
  - ([digit size option](#digit-size-options))
- `maximumFractionDigits`
  - ([digit size option](#digit-size-options))
- `minimumSignificantDigits`
  - ([digit size option](#digit-size-options))
- `maximumSignificantDigits`
  - ([digit size option](#digit-size-options))
- `trailingZeroDisplay`
  - `auto` (default)
  - `stripIfInteger`
- `roundingPriority`
  - `auto` (default)
  - `morePrecision`
  - `lessPrecision`
- `roundingIncrement`
  - 1 (default), 2, 5, 10, 20, 25, 50, 100, 200, 250, 500, 1000, 2000, 2500, and 5000
- `roundingMode`
  - `ceil`
  - `floor`
  - `expand`
  - `trunc`
  - `halfCeil`
  - `halfFloor`
  - `halfExpand` (default)
  - `halfTrunc`
  - `halfEven` 

If the _operand_ of the _expression_ is an implementation-defined type,
such as the _resolved value_ of an _expression_ with a `:number` or `:integer` _annotation_,
it can include option values.
These are included in the resolved option values of the _expression_,
with _options_ on the _expression_ taking priority over any option values of the _operand_.

> For example, the _placeholder_ in this _message_:
> ```
> .input {$n :number notation=scientific minimumFractionDigits=2}
> {{{$n :number minimumFractionDigits=1}}}
> ```
> would be formatted with the resolved options
> `{ notation: 'scientific', minimumFractionDigits: '1' }`.

##### Default Value of `select` Option

The value `plural` is the default for the option `select` 
because it is the most common use case for numeric selection.
It can be used for exact value matches but also allows for the grammatical needs of 
languages using CLDR's plural rules.
This might not be noticeable in the source language (particularly English), 
but can cause problems in target locales that the original developer is not considering.

> For example, a naive developer might use a special message for the value `1` without
> considering a locale's need for a `one` plural:
> ```
> .input {$var :number}
> .match $var
> 1   {{You have one last chance}}
> one {{You have {$var} chance remaining}}
> *   {{You have {$var} chances remaining}}
> ```
>
> The `one` variant is needed by languages such as Polish or Russian.
> Such locales typically also require other keywords such as `two`, `few`, and `many`.

##### Percent Style
When implementing `style=percent`, the numeric value of the _operand_ 
MUST be multiplied by 100 for the purposes of formatting.

> For example,
> ```
> The total was {0.5 :number style=percent}.
> ```
> should format in a manner similar to:
> > The total was 50%.

#### Resolved Value

The _resolved value_ of an _expression_ with a `:number` _function_
contains an implementation-defined numerical value
of the _operand_ of the annotated _expression_,
together with the resolved options' values.

#### Selection

The _function_ `:number` performs selection as described in [Number Selection](#number-selection) below.

### The `:integer` function

The function `:integer` is a selector and formatter for matching or formatting numeric 
values as integers.

#### Operands

The function `:integer` requires a [Number Operand](#number-operands) as its _operand_.

#### Options

Some options do not have default values defined in this specification.
The defaults for these options are implementation-dependent.
In general, the default values for such options depend on the locale, 
the value of other options, or both.

> [!NOTE]
> The names of _options_ and their _values_ were derived from the
> [options](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Intl/NumberFormat/NumberFormat#options)
> in JavaScript's `Intl.NumberFormat`.

The following options and their values are required in the default registry to be available on the 
function `:integer`:
- `select`
   -  `plural` (default)
   -  `ordinal`
   -  `exact`
- `numberingSystem`
   - valid [Unicode Number System Identifier](https://cldr-smoke.unicode.org/spec/main/ldml/tr35.html#UnicodeNumberSystemIdentifier)
     (default is locale-specific)
- `signDisplay`
   -  `auto` (default)
   -  `always`
   -  `exceptZero`
   -  `negative`
   -  `never`
- `style`
  - `decimal` (default)
  - `percent` (see [Percent Style](#percent-style) below)
- `useGrouping`
  - `auto` (default)
  - `always`
  - `never`
  - `min2`
- `minimumIntegerDigits`
  - ([digit size option](#digit-size-options), default: `1`)
- `maximumSignificantDigits`
  - ([digit size option](#digit-size-options))

If the _operand_ of the _expression_ is an implementation-defined type,
such as the _resolved value_ of an _expression_ with a `:number` or `:integer` _annotation_,
it can include option values.
In general, these are included in the resolved option values of the _expression_,
with _options_ on the _expression_ taking priority over any option values of the _operand_.
Option values with the following names are however discarded if included in the _operand_:
- `compactDisplay`
- `notation`
- `minimumFractionDigits`
- `maximumFractionDigits`
- `minimumSignificantDigits`

##### Default Value of `select` Option

The value `plural` is the default for the option `select` 
because it is the most common use case for numeric selection.
It can be used for exact value matches but also allows for the grammatical needs of 
languages using CLDR's plural rules.
This might not be noticeable in the source language (particularly English), 
but can cause problems in target locales that the original developer is not considering.

> For example, a naive developer might use a special message for the value `1` without
> considering a locale's need for a `one` plural:
> ```
> .input {$var :integer}
> .match $var
> 1   {{You have one last chance}}
> one {{You have {$var} chance remaining}}
> *   {{You have {$var} chances remaining}}
> ```
>
> The `one` variant is needed by languages such as Polish or Russian.
> Such locales typically also require other keywords such as `two`, `few`, and `many`.

##### Percent Style
When implementing `style=percent`, the numeric value of the _operand_ 
MUST be multiplied by 100 for the purposes of formatting.

> For example,
> ```
> The total was {0.5 :number style=percent}.
> ```
> should format in a manner similar to:
> > The total was 50%.

#### Resolved Value

The _resolved value_ of an _expression_ with an `:integer` _function_
contains the implementation-defined integer value
of the _operand_ of the annotated _expression_,
together with the resolved options' values.

#### Selection

The _function_ `:integer` performs selection as described in [Number Selection](#number-selection) below.

### The `:math` function

The function `:math` is a selector and formatter for matching or formatting
numeric values to which a mathematical operation has been applied.

> This function is useful for selection and formatting of values that
> differ from the input value by a specified amount.
> For example, it can be used in a message such as this:
> ```
> .input {$like_count :integer}
> .local $others_count = {$like_count :math subtract=1}
> .match $like_count $others_count
> 0 *   {{Your post has no likes.}}
> 1 *   {{{$name} liked your post.}}
> * one {{{$name} and {$others_count} other user liked your post.}}
> * *   {{{$name} and {$others_count} other users liked your post.}}
> ```

#### Operands

The function `:math` requires a [Number Operand](#number-operands) as its _operand_.

#### Options

The options on `:math` are exclusive with each other,
and exactly one option is always required.
The options do not have default values.

The following options and their values are
required in the default registry to be available on the function `:math`:
- `add`
  - ([digit size option](#digit-size-options))
- `subtract`
  - ([digit size option](#digit-size-options))

If no options or more than one option is set,
or if an _option_ value is not a [digit size option](#digit-size-options),
a _Bad Option_ error is emitted
and a _fallback value_ used as the _resolved value_ of the _expression_.

#### Resolved Value

The _resolved value_ of an _expression_ with a `:math` _function_
contains the implementation-defined numeric value
of the _operand_ of the annotated _expression_.

If the `add` option is set,
the numeric value of the _resolved value_ is formed by incrementing
the numeric value of the _operand_ by the integer value of the digit size option value.

If the `subtract` option is set,
the numeric value of the _resolved value_ is formed by decrementing
the numeric value of the _operand_ by the integer value of the digit size option value.

If the _operand_ of the _expression_ is an implementation-defined numeric type,
such as the _resolved value_ of an _expression_ with a `:number` or `:integer` _annotation_,
it can include option values.
These are included in the resolved option values of the _expression_.
The `:math` _options_ are not included in the resolved option values.

> [!NOTE]
> Implementations can encounter practical limits with `:math` _expressions_,
> such as the result of adding two integers exceeding 
> the storage or precision of some implementation-defined number type.
> In such cases, implementations can emit an _Unsupported Operation_ error
> or they might just silently overflow the underlying data value.

#### Selection

The _function_ `:math` performs selection as described in [Number Selection](#number-selection) below.

### The `:currency` function

The function `:currency` is a selector and formatter for currency values, 
which are a specialized form of numeric selection and formatting.

#### Operands

The _operand_ of the `:currency` function can be one of any number of
implementation-defined types,
each of which contains a numerical `value` and a `currency`;
or it can be a [Number Operand](#number-operands), as long as the option
`currency` is provided.
The option `currency` MUST NOT be used to override the currency of an implementation-defined type.
Using this option in such a case results in a _Bad Option_ error.

The value of the _operand_'s `currency` MUST be either a string containing a
well-formed [Unicode Currency Identifier](https://cldr-smoke.unicode.org/spec/main/ldml/tr35.html#UnicodeCurrencyIdentifier)
or an implementation-defined currency type.
Although currency codes are expected to be uppercase,
implementations SHOULD treat them in a case-insensitive manner.
A well-formed Unicode Currency Identifier matches the production `currency_code` in this ABNF:
```abnf
currency_code = 3ALPHA
```

A [Number Operand](#number-operands) without a `currency` _option_ results in a _Bad Operand_ error.

> [!NOTE]
> For example, in ICU4J, the type `com.ibm.icu.util.CurrencyAmount` can be used
> to set the amount and currency.

> [!NOTE]
> The `currency` is only required to be well-formed rather than checked for validity.
> This allows new currency codes to be defined 
> (there are many recent examples of this occuring).
> It also avoids requiring implementations to check currency codes for validity,
> although implementations are permitted to emit _Bad Option_ or _Bad Operand_ for invalid codes.

> [!NOTE]
> For runtime environments that do not provide a ready-made data structure,
> class, or type for currency values, the implementation ought to provide
> a data structure, convenience function, or documentation on how to encode
> the value and currency code for formatting.
> For example, such an implementation might define a "currency operand"
> to include a key-value structure with specific keys to be the
> local currency operand, which might look like the following:
> ```
> {
>    "value": 123.45,
>    "currency": "EUR"
> }
> ```

#### Options

Some options do not have default values defined in this specification.
The defaults for these options are implementation-dependent.
In general, the default values for such options depend on the locale, 
the currency,
the value of other options, or all of these.

Fraction digits for currency values behave differently than for other numeric formatters.
The number of fraction digits displayed is usually set by the currency used.
For example, USD uses 2 fraction digits, while JPY uses none.
Setting some other number of `fractionDigits` allows greater precision display
(such as when performing currency conversions or other specialized operations)
or disabling fraction digits if set to `0`.

The _option_ `trailingZeroDisplay` has a value `stripIfInteger` that is useful 
for displaying currencies with their fraction digits removed when the fraction
part of the _operand_ is zero.
This is sometimes used in _messages_ to make the displayed value omit the fraction part
automatically.
> For example, this _message_:
> ```
> The special price is {$price :currency trailingZeroDisplay=stripIfInteger}.
> ```
> When used with the value `5.00 USD` in the `en-US` locale displays as:
> ```
> The special price is $5.
> ```
> But like this when when value is `5.01 USD`:
> ```
> The special price is $5.01.
> ```

Implementations MAY internally alias option values that they do not have data or a backing implementation for.
Notably, the `currencyDisplay` option has a rich set of values that mirrors developments in CLDR data.
Some implementations might not be able to produce all of these formats for every currency.

> [!NOTE]
> Except where noted otherwise, the names of _options_ and their _values_ were derived from the
> [options](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Intl/NumberFormat/NumberFormat#options)
> in JavaScript's `Intl.NumberFormat`.

> [!NOTE]
> The option `select` does not accept the value `ordinal` because selecting
> currency values using ordinal rules makes no sense.

The following options and their values are required to be available on the function `:currency`:
- `select`
   -  `plural` (default)
   -  `exact`
- `currency`
   - well-formed [Unicode Currency Identifier](https://cldr-smoke.unicode.org/spec/main/ldml/tr35.html#UnicodeCurrencyIdentifier)
     (no default)
- `compactDisplay` (this option only has meaning when combined with the option `notation=compact`)
   - `short` (default)
   - `long`
- `notation`
   - `standard` (default)
   - `compact`
- `numberingSystem`
   - valid [Unicode Number System Identifier](https://cldr-smoke.unicode.org/spec/main/ldml/tr35.html#UnicodeNumberSystemIdentifier)
     (default is locale-specific)
- `currencySign`
  - `accounting`
  - `standard` (default)
- `currencyDisplay`
  - `narrowSymbol`
  - `symbol` (default)
  - `name`
  - `code`
  - `formalSymbol`
  - `never` (this is called `hidden` in ICU)
- `useGrouping`
  - `auto` (default)
  - `always`
  - `never`
  - `min2`
- `minimumIntegerDigits`
  - ([digit size option](#digit-size-options), default: `1`)
- `fractionDigits` (unlike number/integer formats, the fraction digits for currency formatting are fixed)
  - `auto` (default) (the number of digits used by the currency)
  - ([digit size option](#digit-size-options))
- `minimumSignificantDigits`
  - ([digit size option](#digit-size-options))
- `maximumSignificantDigits`
  - ([digit size option](#digit-size-options))
- `trailingZeroDisplay`
  - `auto` (default)
  - `stripIfInteger`
- `roundingPriority`
  - `auto` (default)
  - `morePrecision`
  - `lessPrecision`
- `roundingIncrement`
  - 1 (default), 2, 5, 10, 20, 25, 50, 100, 200, 250, 500, 1000, 2000, 2500, and 5000
- `roundingMode`
  - `ceil`
  - `floor`
  - `expand`
  - `trunc`
  - `halfCeil`
  - `halfFloor`
  - `halfExpand` (default)
  - `halfTrunc`
  - `halfEven`

If the _operand_ of the _expression_ is an implementation-defined type,
such as the _resolved value_ of an _expression_ with a `:currency` _annotation_,
it can include option values.
These are included in the resolved option values of the _expression_,
with _options_ on the _expression_ taking priority over any option values of the _operand_.

> For example, the _placeholder_ in this _message_:
> ```
> .input {$n :currency currency=USD trailingZeroDisplay=stripIfInteger}
> {{{$n :currency currencySign=accounting}}}
> ```
> would be formatted with the resolved options
> `{ currencySign: 'accounting', trailingZeroDisplay: 'stripIfInteger', currency: 'USD' }`.

#### Resolved Value

The _resolved value_ of an _expression_ with a `:currency` _function_
contains an implementation-defined currency value
of the _operand_ of the annotated _expression_,
together with the resolved options' values.

#### Selection

The _function_ `:currency` performs selection as described in [Number Selection](#number-selection) below.

### The `:unit` function

The _function_ `:unit` is **Proposed** for inclusion in the next release of this specification but has not yet been finalized.
The _function_ `:unit` is proposed to be a **RECOMMENDED** selector and formatter for unitized values,
that is, for numeric values associated with a unit of measurement.
This is a specialized form of numeric selection and formatting.

#### Operands

The _operand_ of the `:unit` function can be one of any number of
implementation-defined types,
each of which contains a numerical `value` plus a `unit`
or it can be a [Number Operand](#number-operands), as long as the _option_
`unit` is provided.

The value of the _operand_'s `unit` SHOULD be either a string containing a
valid [Unit Identifier](https://www.unicode.org/reports/tr35/tr35-general.html#unit-identifiers)
or an implementation-defined unit type.

A [Number Operand](#number-operands) without a `unit` _option_ results in a _Bad Operand_ error.

> [!NOTE]
> For example, in ICU4J, the type `com.ibm.icu.util.Measure` might be used
> as an _operand_ for `:unit` because it contains the `value` and `unit`.

> [!NOTE]
> For runtime environments that do not provide a ready-made data structure,
> class, or type for unit values, the implementation ought to provide
> a data structure, convenience function, or documentation on how to encode
> the value and unit for formatting.
> For example, such an implementation might define a "unit operand"
> to include a key-value structure with specific keys to be the
> local unit operand, which might look like the following:
> ```
> {
>    "value": 123.45,
>    "unit": "kilometer-per-hour"
> }
> ```

#### Options

Some _options_ do not have default values defined in this specification.
The defaults for these _options_ are implementation-dependent.
In general, the default values for such _options_ depend on the locale, 
the unit,
the value of other _options_, or all of these.

> [!NOTE]
> The option `select` does not accept the value `ordinal` because selecting
> unit values using ordinal rules makes no sense.

The following options and their values are required to be available on the function `:unit`:
- `select`
   -  `plural` (default)
   -  `exact`
- `unit`
   - valid [Unit Identifier](https://www.unicode.org/reports/tr35/tr35-general.html#unit-identifiers)
     (no default)
- `usage` \[RECOMMENDED\]
    - valid [Unicode Unit Preference](https://www.unicode.org/reports/tr35/tr35-info.html#unit-preferences)
      (no default, see [Unit Conversion](#unit-conversion) below)
- `unitDisplay`
  - `short` (default)
  - `narrow`
  - `long`
- `compactDisplay` (this option only has meaning when combined with the option `notation=compact`)
   - `short` (default)
   - `long`
- `notation`
   - `standard` (default)
   - `compact`
- `numberingSystem`
   - valid [Unicode Number System Identifier](https://cldr-smoke.unicode.org/spec/main/ldml/tr35.html#UnicodeNumberSystemIdentifier)
     (default is locale-specific)
- `signDisplay`
   -  `auto` (default)
   -  `always`
   -  `exceptZero`
   -  `negative`
   -  `never`
- `useGrouping`
  - `auto` (default)
  - `always`
  - `never`
  - `min2`
- `minimumIntegerDigits`
  - ([digit size option](#digit-size-options), default: `1`)
- `minimumFractionDigits`
  - ([digit size option](#digit-size-options))
- `maximumFractionDigits`
  - ([digit size option](#digit-size-options))
- `minimumSignificantDigits`
  - ([digit size option](#digit-size-options))
- `maximumSignificantDigits`
  - ([digit size option](#digit-size-options))
- `roundingPriority`
  - `auto` (default)
  - `morePrecision`
  - `lessPrecision`
- `roundingIncrement`
  - 1 (default), 2, 5, 10, 20, 25, 50, 100, 200, 250, 500, 1000, 2000, 2500, and 5000
- `roundingMode`
  - `ceil`
  - `floor`
  - `expand`
  - `trunc`
  - `halfCeil`
  - `halfFloor`
  - `halfExpand` (default)
  - `halfTrunc`
  - `halfEven`

If the _operand_ of the _expression_ is an implementation-defined type,
such as the _resolved value_ of an _expression_ with a `:unit` _annotation_,
it can include _option_ values.
These are included in the resolved _option_ values of the _expression_,
with _options_ on the _expression_ taking priority over any _option_ values of the _operand_.

> For example, the _placeholder_ in this _message_:
> ```
> .input {$n :unit unit=furlong minimumFractionDigits=2}
> {{{$n :unit minimumIntegerDigits=1}}}
> ```
> would have the resolved options:
> `{ unit: 'furlong', minimumFractionDigits: '2', minimumIntegerDigits: '1' }`.

#### Resolved Value

The _resolved value_ of an _expression_ with a `:unit` _function_
consist of an implementation-defined unit value
of the _operand_ of the annotated _expression_,
together with the resolved _options_ and their resolved values.

#### Selection

The _function_ `:unit` performs selection as described in [Number Selection](#number-selection) below.

#### Unit Conversion

Implementations MAY support conversion to the locale's preferred units via the `usage` _option_.
Implementing this _option_ is optional.
Not all `usage` values are compatible with a given unit.
Implementations SHOULD emit an _Unsupported Operation_ error if the requested conversion is not supported.

> For example, trying to convert a `length` unit (such as "meters")
> to a `volume` usage (which might be a unit akin to "liters" or "gallons", depending on the locale)
> could produce an _Unsupported Operation_ error.

Implementations MUST NOT substitute the unit without performing the associated conversion.

> For example, consider the value:
> ```
> {
>    "value": 123.5,
>    "unit": "meter"
> }
> ```
> The following _message_ might convert the formatted result to U.S. customary units
> in the `en-US` locale:
> ```
> You have {$v :unit usage=road maximumFractionDigits=0} to go.
> ```
> This can produce "You have 405 feet to go."



### Number Operands

The _operand_ of a number function is either an implementation-defined type or
a literal whose contents match the `number-literal` production in the [ABNF](/spec/message.abnf).
All other values produce a _Bad Operand_ error.

> For example, in Java, any subclass of `java.lang.Number` plus the primitive
> types (`byte`, `short`, `int`, `long`, `float`, `double`, etc.) 
> might be considered as the "implementation-defined numeric types".
> Implementations in other programming languages would define different types
> or classes according to their local needs.

> [!NOTE]
> String values passed as variables in the _formatting context_'s
> _input mapping_ can be formatted as numeric values as long as their
> contents match the `number-literal` production in the [ABNF](/spec/message.abnf).
>
> For example, if the value of the variable `num` were the string
> `-1234.567`, it would behave identically to the local
> variable in this example:
> ```
> .local $example = {|-1234.567| :number}
> {{{$num :number} == {$example}}}
> ```

> [!NOTE]
> Implementations are encouraged to provide support for compound types or data structures
> that provide additional semantic meaning to the formatting of number-like values.
> For example, in ICU4J, the type `com.ibm.icu.util.Measure` can be used to communicate
> a value that includes a unit
> or the type `com.ibm.icu.util.CurrencyAmount` can be used to set the currency and related
> options (such as the number of fraction digits).

### Digit Size Options

Some _options_ of number _functions_ are defined to take a "digit size option".
The _function handlers_ for number _functions_ use these _options_ to control aspects of numeric display
such as the number of fraction, integer, or significant digits.

A "digit size option" is an _option_ value that the _function_ interprets
as a small integer value greater than or equal to zero.
Implementations MAY define an upper limit on the _resolved value_
of a digit size option option consistent with that implementation's practical limits.

In most cases, the value of a digit size option will be a string that
encodes the value as a non-negative integer.
Implementations MAY also accept implementation-defined types as the value.
When provided as a string, the representation of a digit size option matches the following ABNF:
>```abnf
> digit-size-option = "0" / (("1"-"9") [DIGIT])
>```

If the value of a digit size option does not evaluate as a non-negative integer,
or if the value exceeds any implementation-defined upper limit
or any option-specific lower limit, a _Bad Option Error_ is emitted.

### Number Selection

Number selection has three modes:
- `exact` selection matches the operand to explicit numeric keys exactly
- `plural` selection matches the operand to explicit numeric keys exactly
  followed by a plural rule category if there is no explicit match
- `ordinal` selection matches the operand to explicit numeric keys exactly
  followed by an ordinal rule category if there is no explicit match

When implementing [`MatchSelectorKeys(resolvedSelector, keys)`](/spec/formatting.md#resolve-preferences)
where `resolvedSelector` is the _resolved value_ of a _selector_
and `keys` is a list of strings,
numeric selectors perform as described below.

1. Let `exact` be the serialized representation of the numeric value of `resolvedSelector`.
   (See [Exact Literal Match Serialization](#exact-literal-match-serialization) for details)
1. Let `keyword` be a string which is the result of [rule selection](#rule-selection) on `resolvedSelector`.
1. Let `resultExact` be a new empty list of strings.
1. Let `resultKeyword` be a new empty list of strings.
1. For each string `key` in `keys`:
   1. If the value of `key` matches the production `number-literal`, then
      1. If `key` and `exact` consist of the same sequence of Unicode code points, then
         1. Append `key` as the last element of the list `resultExact`.
   1. Else if `key` is one of the keywords `zero`, `one`, `two`, `few`, `many`, or `other`, then
      1. If `key` and `keyword` consist of the same sequence of Unicode code points, then
         1. Append `key` as the last element of the list `resultKeyword`.
   1. Else, emit a _Bad Variant Key_ error.
1. Return a new list whose elements are the concatenation of the elements (in order) of `resultExact` followed by the elements (in order) of `resultKeyword`.

> [!NOTE]
> Implementations are not required to implement this exactly as written.
> However, the observed behavior must be consistent with what is described here.

#### Rule Selection

Rule selection is intended to support the grammatical matching needs of different 
languages/locales in order to support plural or ordinal numeric values.

If the _option_ `select` is set to `exact`, rule-based selection is not used.
Otherwise rule selection matches the _operand_, as modified by function _options_, to exactly one of these keywords:
`zero`, `one`, `two`, `few`, `many`, or `other`.
The keyword `other` is the default.

> [!NOTE]
> Since valid keys cannot be the empty string in a numeric expression, returning the
> empty string disables keyword selection.

The meaning of the keywords is locale-dependent and implementation-defined.
A _key_ that matches the rule-selected keyword is a stronger match than the fallback key `*`
but a weaker match than any exact match _key_ value.

The rules for a given locale might not produce all of the keywords.
A given _operand_ value might produce different keywords depending on the locale.

Apply the rules to the _resolved value_ of the _operand_ and the relevant function _options_,
and return the resulting keyword.
If no rules match, return `other`.

If the option `select` is set to `plural`, the rules applied to selection SHOULD be 
the CLDR plural rule data of type `cardinal`. 
See [charts](https://www.unicode.org/cldr/charts/latest/supplemental/language_plural_rules.html)
for examples.

If the option `select` is set to `ordinal`, the rules applied to selection SHOULD be 
the CLDR plural rule data of type `ordinal`. 
See [charts](https://www.unicode.org/cldr/charts/latest/supplemental/language_plural_rules.html)
for examples.

> **Example.**
> In CLDR 44, the Czech (`cs`) plural rule set can be found
> [here](https://www.unicode.org/cldr/charts/44/supplemental/language_plural_rules.html#cs).
>
> A message in Czech might be:
> ```
> .input {$numDays :number}
> .match $numDays
> one  {{{$numDays} den}}
> few  {{{$numDays} dny}}
> many {{{$numDays} dne}}
> *    {{{$numDays} dní}}
> ```
> Using the rules found above, the results of various _operand_ values might look like:
> | Operand value | Keyword | Formatted Message |
> |---|---|---|
> | 1 | `one` | 1 den |
> | 2 | `few` | 2 dny |
> | 5 | `other` | 5 dní |
> | 22 | `few` | 22 dny |
> | 27 | `other` | 27 dní |
> | 2.4 | `many` | 2,4 dne |

#### Exact Literal Match Serialization

If the numeric value of `resolvedSelector` is an integer
and none of the following options are set for `resolvedSelector`,
the serialized form of the numeric value MUST match the ABNF defined below for `integer`,
representing its decimal value:
- `minimumFractionDigits`
- `minimumIntegerDigits`
- `minimumSignificantDigits`
- `maximumSignificantDigits`
- `notation`
- `style`

```abnf
integer = "0" / ["-"] ("1"-"9") *DIGIT
```

Otherwise, the serialized form of the numeric value is implementation-defined.

> [!IMPORTANT]
> The exact behavior of exact literal match is only well defined
> for integer values without leading zeros.
> Functions that use fraction digits or significant digits
> might work in specific implementation-defined ways.
> Users should avoid depending on these types of keys in message selection.

## Date and Time Value Formatting

This subsection describes the functions and options for date/time formatting.
Selection based on date and time values is not required in this release.

> [!NOTE]
> Selection based on date/time types is not required by MF2.
> Implementations should use care when defining selectors based on date/time types.
> The types of queries found in implementations such as `java.time.TemporalAccessor`
> are complex and user expectations may be inconsistent with good I18N practices.

### The `:datetime` function

The function `:datetime` is used to format date/time values, including
the ability to compose user-specified combinations of fields.

If no options are specified, this function defaults to the following:
- `{$d :datetime}` is the same as `{$d :datetime dateStyle=medium timeStyle=short}`

> [!NOTE]
> The default formatting behavior of `:datetime` is inconsistent with `Intl.DateTimeFormat`
> in JavaScript and with `{d,date}` in ICU MessageFormat 1.0.
> This is because, unlike those implementations, `:datetime` is distinct from `:date` and `:time`.

#### Operands

The _operand_ of the `:datetime` function is either 
an implementation-defined date/time type
or a _date/time literal value_, as defined in [Date and Time Operand](#date-and-time-operands).
All other _operand_ values produce a _Bad Operand_ error.

#### Options

The `:datetime` function can use either the appropriate _style options_ 
or can use a collection of _field options_ (but not both) to control the formatted 
output.
_Date/time override options_ can be combined with either _style options_ or _field options_.

If both _style options_ and _field options_ are specified,
a _Bad Option_ error is emitted
and a _fallback value_ used as the _resolved value_ of the _expression_.

If the _operand_ of the _expression_ is an implementation-defined date/time type,
it can include _style options_, _field options_, or other option values.
These are included in the resolved option values of the _expression_,
with _options_ on the _expression_ taking priority over any option values of the _operand_.

> [!NOTE]
> The names of _options_ and their _values_ were derived from the
> [options](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Intl/DateTimeFormat/resolvedOptions#description)
> in JavaScript's `Intl.DateTimeFormat`.

##### Style Options

The function `:datetime` has these _style options_.
- `dateStyle`
  - `full`
  - `long`
  - `medium`
  - `short`
- `timeStyle`
  - `full`
  - `long`
  - `medium`
  - `short`

##### Field Options

_Field options_ describe which fields to include in the formatted output
and what format to use for that field.

> [!NOTE]
> _Field options_ do not have default values because they are only to be used
> to compose the formatter.

The _field options_ are defined as follows:

> [!IMPORTANT]
> The value `2-digit` for some _field options_ MUST be quoted
> in the MessageFormat syntax because it starts with a digit
> but does not match the `number-literal` production in the ABNF.
> ```
> .local $correct = {$someDate :datetime year=|2-digit|}
> .local $syntaxError = {$someDate :datetime year=2-digit}
> ```

The function `:datetime` has the following options:
- `weekday`
  - `long`
  - `short`
  - `narrow`
- `era`
  - `long`
  - `short`
  - `narrow`
- `year`
  - `numeric`
  - `2-digit`
- `month`
  - `numeric`
  - `2-digit`
  - `long`
  - `short`
  - `narrow`
- `day`
  - `numeric`
  - `2-digit`
- `hour`
  - `numeric`
  - `2-digit`
- `minute`
  - `numeric`
  - `2-digit`
- `second`
  - `numeric`
  - `2-digit`
- `fractionalSecondDigits`
  - `1`
  - `2`
  - `3`
- `timeZoneName`
  - `long`
  - `short`
  - `shortOffset`
  - `longOffset`
  - `shortGeneric`
  - `longGeneric`

#### Resolved Value

The _resolved value_ of an _expression_ with a `:datetime` _function_
contains an implementation-defined date/time value
of the _operand_ of the annotated _expression_,
together with the resolved options values.

### The `:date` function

The function `:date` is used to format the date portion of date/time values.

If no options are specified, this function defaults to the following:
- `{$d :date}` is the same as `{$d :date style=medium}`

#### Operands

The _operand_ of the `:date` function is either 
an implementation-defined date/time type
or a _date/time literal value_, as defined in [Date and Time Operand](#date-and-time-operands).
All other _operand_ values produce a _Bad Operand_ error.

#### Options

The function `:date` has these _options_:
- `style`
  - `full`
  - `long`
  - `medium` (default)
  - `short`
- _Date/time override options_

If the _operand_ of the _expression_ is an implementation-defined date/time type,
it can include other option values.
Any _operand_ option values matching the `:datetime` _style options_ or _field options_ are ignored,
as is any `style` option.

#### Resolved Value

The _resolved value_ of an _expression_ with a `:date` _function_
is implementation-defined.

An implementation MAY emit a _Bad Operand_ or _Bad Option_ error (as appropriate)
when a _variable_ annotated directly or indirectly by a `:date` _annotation_
is used as an _operand_ or an _option_ value.

### The `:time` function

The function `:time` is used to format the time portion of date/time values.

If no options are specified, this function defaults to the following:
- `{$t :time}` is the same as `{$t :time style=short}`

#### Operands

The _operand_ of the `:time` function is either 
an implementation-defined date/time type
or a _date/time literal value_, as defined in [Date and Time Operand](#date-and-time-operands).
All other _operand_ values produce a _Bad Operand_ error.

#### Options

The function `:time` has these _options_:
- `style`
  - `full`
  - `long`
  - `medium`
  - `short` (default)
- _Date/time override options_

If the _operand_ of the _expression_ is an implementation-defined date/time type,
it can include other option values.
Any _operand_ option values matching the `:datetime` _style options_ or _field options_ are ignored,
as is any `style` option.

#### Resolved Value

The _resolved value_ of an _expression_ with a `:time` _function_
is implementation-defined.

An implementation MAY emit a _Bad Operand_ or _Bad Option_ error (as appropriate)
when a _variable_ annotated directly or indirectly by a `:time` _annotation_
is used as an _operand_ or an _option_ value.

### Date and Time Operands

The _operand_ of a date/time function is either 
an implementation-defined date/time type
or a _date/time literal value_, as defined below.
All other _operand_ values produce a _Bad Operand_ error.

A **_<dfn>date/time literal value</dfn>_** is a non-empty string consisting of an ISO 8601 date,
or an ISO 8601 datetime optionally followed by a timezone offset.
As implementations differ slightly in their parsing of such strings,
ISO 8601 date and datetime values not matching the following regular expression MAY also be supported.
Furthermore, matching this regular expression does not guarantee validity,
given the variable number of days in each month.

```regexp
(?!0000)[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])(T([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9](\.[0-9]{1,3})?(Z|[+-]((0[0-9]|1[0-3]):[0-5][0-9]|14:00))?)?
```

When the time is not present, implementations SHOULD use `00:00:00` as the time.
When the offset is not present, implementations SHOULD use a floating time type
(such as Java's `java.time.LocalDateTime`) to represent the time value.
For more information, see [Working with Timezones](https://w3c.github.io/timezone).

> [!IMPORTANT]
> The [ABNF](/spec/message.abnf) and [syntax](/spec/syntax.md) of MF2
> do not formally define date/time literals. 
> This means that a _message_ can be syntactically valid but produce
> a _Bad Operand_ error at runtime.

> [!NOTE]
> String values passed as variables in the _formatting context_'s
> _input mapping_ can be formatted as date/time values as long as their
> contents are date/time literals.
>
> For example, if the value of the variable `now` were the string
> `2024-02-06T16:40:00Z`, it would behave identically to the local
> variable in this example:
> ```
> .local $example = {|2024-02-06T16:40:00Z| :datetime}
> {{{$now :datetime} == {$example}}}
> ```

> [!NOTE]
> True time zone support in serializations is expected to coincide with the adoption
> of Temporal in JavaScript.
> The form of these serializations is known and is a de facto standard.
> Support for these extensions is expected to be required in the post-tech preview.
> See: https://datatracker.ietf.org/doc/draft-ietf-sedate-datetime-extended/


### Date and Time Override Options

**_<dfn>Date/time override options</dfn>_** are _options_ that allow an _expression_ to
override values set by the current locale,
or provided by the _formatting context_ (such as the default time zone),
or embedded in an implementation-defined date/time _operand_ value.

> [!NOTE]
> These _options_ do not have default values because they are only to be used
> as overrides for locale-and-value dependent implementation-defined defaults.

The following _option_ and its values are REQUIRED to be available on
the functions `:datetime` and `:time`:

- `hour12`
  - `true`
  - `false`

The following _options_ and their values are RECOMMENDED to be available on
the functions `:datetime`, `:date`, and `:time`.

- `calendar`
  - valid [Unicode Calendar Identifier](https://cldr-smoke.unicode.org/spec/main/ldml/tr35.html#UnicodeCalendarIdentifier)
- `numberingSystem`
  - valid [Unicode Number System Identifier](https://cldr-smoke.unicode.org/spec/main/ldml/tr35.html#UnicodeNumberSystemIdentifier)

The following _option_ and its values are **Proposed** for
inclusion in the next release of this specification but have not yet been
finalized.
If accepted, implementations could be REQUIRED to make this _option_
available in the functions `:datetime`, `:date`, and `:time`.

- `timeZone`
  - A valid time zone identifier
    (see [TZDB](https://www.iana.org/time-zones)
    and [LDML](https://www.unicode.org/reports/tr35/tr35-dates.html#Time_Zone_Names)
    for information on identifiers)
  - `local`
  - `UTC`
  
> [!NOTE]
> The value `local` permits a _message_ to convert a date/time value
> into a [floating](https://www.w3.org/TR/timezone/#floating) time value
> (sometimes called a _plain_ or _local_ time value) by removing
> the association with a specific time zone.
