# SorryIBrokeYourKryo

Sorry! Kryo serialization of libGDX Array changed after 1.12.1. This shows how to hack it back into compatibility
with libGDX 1.13.1 . It likely will only build if you're targeting language level 8, which is the max supported by
RoboVM anyway.

The general summary of how this works is that it replaces the Array class from 1.13.1 with a 1.12.1-compatible
replacement. The change in 1.13.1 (that I made) was a good one, and it prevents nasty bugs in some cases, but it also
changes the binary serialization of Array, OrderedMap, OrderedSet, and anything else that uses Array from libGDX.
If you already have serialized data, likely with Kryo, that stores the `Array.iterable`, `Array.predicateIterable`,
`ArrayIterable.iterator1`, and/or `ArrayIterable.iterator2` fields, then the change in 1.13.1 makes those fields not
stored anymore (they are marked `transient`). These fields should *probably not be stored* in future code, but if you
have saved data from libGDX 1.12.1, you need the same fields to be compatible.

Note that to be compatible with the current libGDX, you need a *different* replacement Array class.
I'm working on that.

An alternative would be to have used custom serializers from the start, such as those in
[kryo-more](https://github.com/tommyettinger/kryo-more), which are more efficient for types like OrderedMap and
OrderedSet. They also won't break when internal fields change.

# License
[The Apache License version 2.0](LICENSE). This uses code from libGDX with only minor changes, so it keeps the license
of libGDX.
