@file:Suppress("UNUSED_PARAMETER")
package lesson6.task1

import lesson1.task1.sqr

/**
 * Точка на плоскости
 */

data class Point(val x: Double, val y: Double) {
    /**
     * Пример
     *
     * Рассчитать (по известной формуле) расстояние между двумя точками
     */
    fun distance(other: Point): Double = Math.sqrt(sqr(x - other.x) + sqr(y - other.y))
}

/**
 * Треугольник, заданный тремя точками
 */
data class Triangle(val a: Point, val b: Point, val c: Point) {
    /**
     * Пример: полупериметр
     */
    fun halfPerimeter() = (a.distance(b) + b.distance(c) + c.distance(a)) / 2.0

    /**
     * Пример: площадь
     */
    fun area(): Double {
        val p = halfPerimeter()
        return Math.sqrt(p * (p - a.distance(b)) * (p - b.distance(c)) * (p - c.distance(a)))
    }

    /**
     * Пример: треугольник содержит точку
     */
    fun contains(p: Point): Boolean {
        val abp = Triangle(a, b, p)
        val bcp = Triangle(b, c, p)
        val cap = Triangle(c, a, p)
        return abp.area() + bcp.area() + cap.area() <= area()
    }
}

/**
 * Окружность с заданным центром и радиусом
 */
data class Circle(val center: Point, val radius: Double) {
    /**
     * Простая
     *
     * Рассчитать расстояние между двумя окружностями.
     * Расстояние между непересекающимися окружностями рассчитывается как
     * расстояние между их центрами минус сумма их радиусов.
     * Расстояние между пересекающимися окружностями считать равным 0.0.
     */
    fun distance(other: Circle): Double {
        val s = center.distance(other.center) - (radius+other.radius)
        return if (s <= 0.0) 0.0 else s
    }

    /**
     * Тривиальная
     *
     * Вернуть true, если и только если окружность содержит данную точку НА себе или ВНУТРИ себя
     */
    fun contains(p: Point): Boolean = p.distance(center) <= radius
}

/**
 * Отрезок между двумя точками
 */
data class Segment(val begin: Point, val end: Point)

/**
 * Средняя
 *
 * Дано множество точек. Вернуть отрезок, соединяющий две наиболее удалённые из них.
 * Если в множестве менее двух точек, бросить IllegalArgumentException
 */
fun diameter(vararg points: Point): Segment {
    if (points.count() > 1) {
        var max = Segment(points[0], points[1])
        for (i in 0..points.size - 2)
            for (j in i + 1..points.size - 1)
                if (points[i].distance(points[j]) > max.begin.distance(max.end))
                    max = Segment(points[i], points[j])
        return max
    } else throw java.lang.IllegalArgumentException()
}

/**
 * Простая
 *
 * Построить окружность по её диаметру, заданному двумя точками
 * Центр её должен находиться посередине между точками, а радиус составлять половину расстояния между ними
 */
fun circleByDiameter(diameter: Segment): Circle {
    val centerX = diameter.begin.x + (diameter.end.x - diameter.begin.x) / 2
    val centerY = diameter.begin.y + (diameter.end.y - diameter.begin.y) / 2
    val center = Point(centerX, centerY)
    return Circle(center, center.distance(diameter.begin))
}

/**
 * Прямая, заданная точкой и углом наклона (в радианах) по отношению к оси X.
 * Уравнение прямой: (y - point.y) * cos(angle) = (x - point.x) * sin(angle)
 */
data class Line(val point: Point, val angle: Double) {
    /**
     * Средняя
     *
     * Найти точку пересечения с другой линией.
     * Для этого необходимо составить и решить систему из двух уравнений (каждое для своей прямой)
     */
    fun crossPoint(other: Line): Point {
        val k1 = Math.tan(this.angle)
        val b1 = -1 * (this.point.x * k1) + this.point.y
        val k2 = Math.tan(other.angle)
        val b2 = -1 * (other.point.x * k2) + other.point.y
        val x = (b2 - b1) / (k1 - k2)
        val y =
        when {
            (this.angle == Math.PI / 2 || this.angle == Math.PI / -2) -> other.point.y
            (other.angle == Math.PI / 2 || other.angle == Math.PI / -2) -> this.point.y
            else -> (x - this.point.x) * k1 + this.point.y
        }
        return Point(x, y)
    }
}

/**
 * Средняя
 *
 * Построить прямую по отрезку
 */
fun lineBySegment(s: Segment): Line = Line(s.begin, Math.atan((s.end.y - s.begin.y) / (s.end.x - s.begin.x)))

/**
 * Средняя
 *
 * Построить прямую по двум точкам
 */
fun lineByPoints(a: Point, b: Point): Line = lineBySegment(Segment(a, b))

/**
 * Сложная
 *
 * Построить серединный перпендикуляр по отрезку или по двум точкам
 */
fun bisectorByPoints(a: Point, b: Point): Line {
    val segment = Segment(a, b)
    val center = circleByDiameter(segment).center
    val angle = lineBySegment(segment).angle + Math.PI / 2
    return Line(center, angle)
}

/**
 * Средняя
 *
 * Задан список из n окружностей на плоскости. Найти пару наименее удалённых из них.
 * Если в списке менее двух окружностей, бросить IllegalArgumentException
 */
fun findNearestCirclePair(vararg circles: Circle): Pair<Circle, Circle> {
    var minLength = Double.POSITIVE_INFINITY
    var f = circles[0]
    var s = circles[0]
    if (circles.count() > 1) {
        for (i in 0..circles.count() - 1) {
            for (j in i + 1..circles.count() - 1) {
                val dist = circles[i].distance(circles[j])
                if (dist < minLength) {
                    minLength = dist
                    f = circles[i]
                    s = circles[j]
                }
            }
        }
        return Pair(f,s)
    } else throw IllegalArgumentException()

}

/**
 * Очень сложная
 *
 * Дано три различные точки. Построить окружность, проходящую через них
 * (все три точки должны лежать НА, а не ВНУТРИ, окружности).
 * Описание алгоритмов см. в Интернете
 * (построить окружность по трём точкам, или
 * построить окружность, описанную вокруг треугольника - эквивалентная задача).
 */
fun circleByThreePoints(a: Point, b: Point, c: Point): Circle {
    //Если использовать решение через серединные перпендикуляры
    val ma = (b.y - a.y) / (b.x - a.x)
    val mb = (c.y - b.y) / (c.x - b.x)
    val x = (ma * mb * (a.y - c.y) + mb * (a.x + b.x) - ma * (b.x + c.x)) / (2 * (mb - ma))
    val y = -1 / ma * (x - (a.x + b.x) / 2) + (a.y + b.y) / 2
    val p = Point(x,y)
    //Если не выбирать максимум из расстояний до точек, то остается погрешность порядка 4E-16
    var r = Math.max(p.distance(a),p.distance(b))
    r = Math.max(r,p.distance(c))
    return Circle(p,r)
}

/**
 * Очень сложная
 *
 * Дано множество точек на плоскости. Найти круг минимального радиуса,
 * содержащий все эти точки. Если множество пустое, бросить IllegalArgumentException.
 * Если множество содержит одну точку, вернуть круг нулевого радиуса с центром в данной точке.
 *
 * Примечание: в зависимости от с    /*
    val center = bisectorByPoints(a, b).crossPoint(bisectorByPoints(b, c))
    return Circle(center, center.distance(a))
    */итуации, такая окружность может либо проходить через какие-либо
 * три точки данного множества, либо иметь своим диаметром отрезок,
 * соединяющий две самые удалённые точки в данном множестве.
 */
fun minContainingCircle(vararg points: Point): Circle {
    val d = diameter(*points)
    var c = circleByDiameter(d)
    for (p in points) {
        if (!c.contains(p)) {
            c = circleByThreePoints(d.begin, d.end, p)
        }
    }
    return c
}

