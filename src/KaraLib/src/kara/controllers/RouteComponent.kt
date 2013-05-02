package kara.internal

import kara.*

fun String.toRouteComponents() : List<RouteComponent> {
    return this.split("/").map {
        it -> RouteComponent.create(it)
    }
}

/** Base class for objects that represent a single component of a route. */
abstract class RouteComponent(val componentText: String) {
    class object {
        fun create(component: String) : RouteComponent {
            if (component.length > 1 && component.charAt(0) == ':' && component.lastIndexOf(':') == 0)
                return ParamRouteComponent(component)
            else if (component == "*")
                return WildcardRouteComponent(component)
            else
                return StringRouteComponent(component)
        }
    }

    abstract fun matches(value: String) : Boolean

    abstract fun getParam(params : RouteParameters, component: String)
}

/** Route component for a literal string. */
class StringRouteComponent(componentText: String) : RouteComponent(componentText) {
    override fun matches(value: String) : Boolean {
        return value.equalsIgnoreCase(componentText)
    }

    override fun getParam(params : RouteParameters, component: String) {
    }
}

/** Route component for a named parameter. */
class ParamRouteComponent(componentText: String) : RouteComponent(componentText) {
    val name = componentText.substring(1)

    override fun matches(value: String) : Boolean {
        return true
    }

    override fun getParam(params : RouteParameters, component: String) {
        params[name] = component
    }
}

/** Route component for an unnamed parameter. */
class WildcardRouteComponent(componentText: String) : RouteComponent(componentText) {

    override fun matches(value: String) : Boolean {
        return true
    }

    override fun getParam(params : RouteParameters, component: String) {
        params.append(component)
    }
}