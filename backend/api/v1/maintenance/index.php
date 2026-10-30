<?php
require_once __DIR__ . "/../../../config/db.php";
require_once __DIR__ . "/../../../config/auth_helper.php";

$userId = getAuthenticatedUserId($conn);
$method = $_SERVER['REQUEST_METHOD'];

if ($method === 'GET') {
    if (isset($conn) && $conn !== null) {
        $stmt = $conn->prepare("SELECT * FROM maintenance WHERE user_id = :user_id ORDER BY id DESC");
        $stmt->bindParam(":user_id", $userId);
        $stmt->execute();
        $records = $stmt->fetchAll(PDO::FETCH_ASSOC);

        $resultList = array_map(function($m) {
            return [
                "id" => (string)$m['id'],
                "title" => $m['title'],
                "category" => $m['category'],
                "date" => $m['date'],
                "odometerKm" => (int)$m['odometer_km'],
                "notes" => $m['notes'],
                "isCompleted" => (bool)$m['is_completed']
            ];
        }, $records);

        echo json_encode(["success" => true, "message" => "Maintenance records retrieved", "data" => $resultList]);
        exit();
    }

    echo json_encode([
        "success" => true,
        "message" => "Maintenance records retrieved",
        "data" => [
            ["id" => "1", "title" => "Engine Oil & Filter Change", "category" => "Engine Oil", "date" => "15 Oct 2026", "odometerKm" => 14000, "notes" => "Replaced with 15W50 Synthetic", "isCompleted" => true]
        ]
    ]);
} else if ($method === 'POST') {
    $data = json_decode(file_get_contents("php://input"));
    if (!empty($data->title)) {
        if (isset($conn) && $conn !== null) {
            $stmt = $conn->prepare("INSERT INTO maintenance (user_id, title, category, date, odometer_km, notes, is_completed) VALUES (:user_id, :title, :category, :date, :odo, :notes, :completed)");
            $stmt->bindParam(":user_id", $userId);
            $stmt->bindParam(":title", $data->title);
            $cat = isset($data->category) ? $data->category : "General";
            $date = isset($data->date) ? $data->date : "Today";
            $odo = isset($data->odometerKm) ? (int)$data->odometerKm : 14250;
            $notes = isset($data->notes) ? $data->notes : "";
            $completed = isset($data->isCompleted) && !$data->isCompleted ? 0 : 1;
            $stmt->bindParam(":category", $cat);
            $stmt->bindParam(":date", $date);
            $stmt->bindParam(":odo", $odo);
            $stmt->bindParam(":notes", $notes);
            $stmt->bindParam(":completed", $completed);
            $stmt->execute();
            $newId = $conn->lastInsertId();

            echo json_encode([
                "success" => true,
                "message" => "Maintenance record added",
                "data" => ["id" => (string)$newId, "title" => $data->title, "category" => $cat, "date" => $date, "odometerKm" => $odo, "notes" => $notes, "isCompleted" => true]
            ]);
            exit();
        }
    }
    echo json_encode(["success" => true, "message" => "Maintenance record added"]);
}
?>
