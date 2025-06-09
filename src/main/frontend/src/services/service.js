import colors from "../shared/dto/colors";
import course_purple from "../images/course_purple.svg";
import course_red from "../images/course_red.svg";
import course_green from "../images/course_green.svg";
export class Services {
    getCourses() {
        return [
            {
                id: 1,
                name: "A",
                image_color: course_green,
                text_color: colors.GREEN_300,
            },
            {
                id: 2,
                name: "B",
                image_color: course_red,
                text_color: colors.RED_600,
            },
            {
                id: 3,
                name: "C",
                image_color: course_purple,
                text_color: colors.PURPLE,
            },
            {
                id: 4,
                name: "D",
                image_color: course_green,
                text_color: colors.GREEN_300,
            },
            {
                id: 5,
                name: "E",
                image_color: course_red,
                text_color: colors.RED_600,
            },
            {
                id: 6,
                name: "F",
                image_color: course_purple,
                text_color: colors.PURPLE,
            },
            {
                id: 7,
                name: "G",
                image_color: course_green,
                text_color: colors.GREEN_300,
            },
            {
                id: 8,
                name: "H",
                image_color: course_red,
                text_color: colors.RED_600,
            },
        ]
    }
}