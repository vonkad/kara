package kara.internal

import kara.*

fun String?.asNotEmpty(): String? = if (this == null) null else if (!isEmpty()) this else null

fun String.appendPathElement(part : String) = buildString {
    append(this@appendPathElement)
    if (!this.endsWith("/")) {
        append("/")
    }

    if (part.startsWith('/')) {
        append(part.substring(1))
    }
    else {
        append(part)
    }
}

fun Class<*>.routePrefix(): String {
    val owner = enclosingClass
    val defaultPart = if (owner == null) "" else simpleName.toLowerCase()
    val part = getAnnotation(Location::class.java)?.path.asNotEmpty() ?: defaultPart

    val base = if (owner == null) "" else owner.routePrefix()
    return base.appendPathElement(part)
}

fun Class<out Resource>.route(): ResourceDescriptor {
    fun p(part: String) = (enclosingClass?.routePrefix()?:"").appendPathElement(part.replace("#", simpleName.toLowerCase()))
    for (ann in annotations) {
        when (ann) {
            is Get -> return ResourceDescriptor(HttpMethod.GET, p(ann.route), this, null)
            is Post -> return ResourceDescriptor(HttpMethod.POST, p(ann.route), this, ann.allowCrossOrigin)
            is Put -> return ResourceDescriptor(HttpMethod.PUT, p(ann.route), this, ann.allowCrossOrigin)
            is Delete -> return ResourceDescriptor(HttpMethod.DELETE, p(ann.route), this, ann.allowCrossOrigin)
            is Route -> return ResourceDescriptor(ann.method, p(ann.route), this, ann.allowCrossOrigin)
        }
    }

    throw RuntimeException("No HTTP method annotation found in $name")
}
