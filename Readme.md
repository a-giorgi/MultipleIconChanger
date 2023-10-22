# Multiple Icon Changer
This Java-based software allows the user to modify directory icons and their respective subdirectories. It offers the flexibility to assign distinct icons by utilizing image files located within each directory. Users have the option to specify their preferred image, but if it's not found, the software intelligently defaults to the first available image.

## Usage Instructions

1. Begin by specifying the target directory in the "Choose target directory..." text field.
2. Define the desired icon file name in the "Icon file name" field. This will be the image used as icon and it must be contained inside every directory that will have the icon changed.
3. Set the recursion level by entering a number in the corresponding text field.
    - A value of 0 will only change the icon of the target directory.
    - A value of 1 will change the icons of the subdirectories within the target directory.
    - A value of 2 will extend this operation to all subdirectories within those subdirectories, and so on.

If you want to go down deep to every directory contained inside the target, tick the "Set icon for every subdirectory" checkbox, which will ignore the specified recursion level.

Icons generated will be saved in the directory indicated next to "Choose where to save icons..."

## Additional Features

- Enable "Use as prefix" to search for the first image file whose name begins with the text specified in the "Icon file name" field.
- Activate "Use the first image if the file is not available" to use the first available image file within the target directory if a file with the specified name is not found.
- Check the "Square the image" box to crop the image to a square size before applying it as an icon.

## Libraries Utilized

- [Image4j](https://image4j.sourceforge.net/)